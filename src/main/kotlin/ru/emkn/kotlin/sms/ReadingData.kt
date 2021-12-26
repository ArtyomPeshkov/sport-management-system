package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.universalC
import java.io.File

/** абстрактный класс для считывания различных csv-файлов */
abstract class Reader(configurationFolderPath: String) {
    private val configurationFolder: List<File>

    init {
        this.configurationFolder = File(configurationFolderPath).walk().toList()
    }

    fun universalParser(name: String): List<File> {
        val filePath = "${configurationFolder[0]}${File.separator}$name"
        val folder = configurationFolder.find { it.path.contains(filePath) } ?: throw NotEnoughConfigurationFiles(name)
        parseLogger.universalC(Colors.BLUE._name, "reading from  $filePath", 'i')
        return folder.walk().toList().filter { it.extension == "csv" }
    }
}

/** считывает csv-файлы с заявочными протоколами */
class TeamReader(configurationFolderName: String) : Reader(configurationFolderName) {
    fun getTeams(): List<Team> = universalParser("applications").map { getParticularTeam(readFile(it.path)) }
    fun getParticularTeam(file: File): Team {
        val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
        if (fileStrings.isEmpty())
            throw CSVStringWithNameException(file.path)
        val name = fileStrings[0].let {
            if (it.size != 5)
                throw CSVStringWithNameException(file.path)
            else it[0]
        }
        val team = Team(name)
        team.addParticipants(participantsParser(name, file))
        return team
    }
}

/** считывает csv-файлы с протоколами прохождения контрольных точек */
class ControlPointReader(configurationFolderName: String) : Reader(configurationFolderName) {
    fun getPoints(): Map<Int, List<ControlPointWithTime>> {
        val res: MutableList<Pair<Int, ControlPointWithTime>> = mutableListOf()
        universalParser("points").forEach { res += parseCP(it) }
        return res.groupBy({ it.first }, { it.second })
    }

    fun parseCP(protocol: File): List<Pair<Int, ControlPointWithTime>> {
        val fileFirstString = csvReader().readAll(protocol.readText().substringBefore("\n"))
        val nameOfControlPoint = fileFirstString[0].let {
            if (it.size != 2)
                throw CSVStringWithNameException(protocol.path)
            else it[0]
        }
        val eachControlPoint = csvReader().readAll(protocol).drop(1)
        return eachControlPoint.map {
            Pair(
                it[0].toInt(),
                ControlPointWithTime(ControlPoint(nameOfControlPoint), Time(it[1]))
            )
        }
    }
}

/** считывает csv-файл со списком дистанций и контрольных точек для каждой из них */
class DistanceReader(configurationFolderName: String) : Reader(configurationFolderName) {
    fun getDistances(controlPoints: MutableList<ControlPoint> = mutableListOf()): Map<String, Distance> {
        val distances = universalParser("distances.csv")[0]
        val distanceStrings = csvReader().readAllWithHeader(distances)
        return distanceStrings.associate {
            val distanceName = it["Название"] ?: throw CSVFieldNamesException(distances.path)
            Pair(
                distanceName,
                getDistance(distanceName, it, controlPoints)
            )
        }
    }
}

/** считывает csv-файл со списком из групп и соответствующих каждой дистанцией */
class GroupReader(configurationFolderName: String) : Reader(configurationFolderName) {
    fun getGroups(distanceList: Map<String, Distance>): List<Group> {
        val groups = universalParser("groups.csv")[0]
        val groupStrings = csvReader().readAllWithHeader(groups)
        return groupStrings.map { groupData ->
            val groupName = groupData["Название"] ?: throw CSVFieldNamesException(groups.path)
            val distance = distanceList[groupData["Дистанция"]]
                ?: throw UnexpectedValueException("Нет такой дистанции ${groupData["Дистанция"]}")

            val group = Group(groupName, distance)
            group.modifyGroup(groupData, groups.path)

            group
        }.toSet().toList()
    }
}

/** считывает csv-файлы с протоколами рещультатов для групп */
class ResultsReader(configurationFolderName: String) : Reader(configurationFolderName) {

    fun getGroupsFromResultProtocols(): MutableList<Team> {
        val resultsFolder = universalParser("results")
        val teams = mutableListOf<Team>()
        resultsFolder.forEach { parseResultProtocol(it, teams) }
        return teams
    }

    fun parseResultProtocol(protocol: File, teams: MutableList<Team>) {
        val fileStrings = csvReader().readAll(protocol.readText())
        val nameOfGroup = fileStrings[0].let {
            if (it[0] == "")
                throw CSVStringWithNameException(protocol.path)
            else it[0]
        }
        val bestResult: Time = try {
            Time(fileStrings[2][8])
        } catch (e: IllegalTimeFormatException) {
            Time(0)
        }

        val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
        participantData.forEachIndexed { i, it ->
            val participant = makeParticipant(it, i, protocol.path, "", nameOfGroup)
            val currentTime = it["Результат"] ?: throw CSVFieldNamesException(protocol.path)
            try {
                participant.setPoints(
                    Integer.max(
                        0,
                        (100.0 * (2 - Time(currentTime).timeInSeconds.toDouble() / bestResult.timeInSeconds)).toInt()
                    )
                )
            } catch (e: IllegalTimeFormatException) {
                participant.setParticipantStatus("Снят")
                participant.setPoints(0)
            }
            val collectiveName = it["Коллектив"] ?: throw CSVFieldNamesException(protocol.path)
            if (teams.find { it.name == collectiveName } == null) teams.add(Team(collectiveName))
            val collective =
                teams.find { it.name == collectiveName } ?: throw UnexpectedValueException(collectiveName)
            collective.addParticipant(ParticipantStart(participant))
        }
    }
}

/** считывает csv-файлы со стартовыми протоколами */
class StartProtocolParse(configurationFolderName: String) : Reader(configurationFolderName) {
    fun getStartProtocolFolder(groups: List<Group>) {
        val startInfo = universalParser("starts")
        startInfo.map { parseStartProtocol(it, groups) }
    }

    fun parseStartProtocol(protocol: File, groups: List<Group>) {
        val fileStrings = csvReader().readAll(protocol.readText().substringBefore("\n"))
        val nameOfGroup = fileStrings[0].let {
            if (it[0] == "")
                throw CSVStringWithNameException(protocol.path)
            else it[0]
        }
        val indexOfGroup = getGroupIndexByName(nameOfGroup, groups)
        val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
        participantData.forEachIndexed { i, it ->
            val participant = ParticipantStart(makeParticipant(it, i, protocol.path, "", nameOfGroup))
            participant.setStart(
                it["Номер"]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
                Time(it["Стартовое время"] ?: throw CSVFieldNamesException(protocol.path))
            )
            groups[indexOfGroup].addParticipant(participant)

        }
    }
}

class GroupResultsReader(configurationFolderName: String) : Reader(configurationFolderName) {
    fun getGroupsFromResultProtocols(): MutableMap<String, List<ParticipantResult>> {
        val resultsFolder = universalParser("results")
        val groups = mutableMapOf<String, MutableList<ParticipantResult>>()
        resultsFolder.forEach { parseResultProtocol(it, groups) }
        return groups.mapValues{it.value.toList()}.toMutableMap()
    }

    fun parseResultProtocol(protocol: File, groupsMap: MutableMap<String, MutableList<ParticipantResult>>) {
        val fileStrings = csvReader().readAll(protocol.readText())
        val nameOfGroup = fileStrings[0].let {
            if (it[0] == "")
                throw CSVStringWithNameException(protocol.path)
            else it[0]
        }
        if (groupsMap[nameOfGroup].isNullOrEmpty())
            groupsMap[nameOfGroup] = mutableListOf()
        val bestResult: Time = try {
            Time(fileStrings[2][8])
        } catch (e: IllegalTimeFormatException) {
            Time(0)
        }

        val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
        participantData.forEachIndexed { i, it ->
            val participant = makeParticipant(it, i, protocol.path, "", nameOfGroup)
            val currentTime = it["Результат"] ?: throw CSVFieldNamesException(protocol.path)
            try {
                participant.setPoints(
                    Integer.max(
                        0,
                        (100.0 * (2 - Time(currentTime).timeInSeconds.toDouble() / bestResult.timeInSeconds)).toInt()
                    )
                )
            } catch (e: IllegalTimeFormatException) {
                participant.setParticipantStatus("Снят")
                participant.setPoints(0)
            }
            val collectiveName = it["Коллектив"] ?: throw CSVFieldNamesException(protocol.path)
            participant.setTeam(collectiveName)
            val participantStart = ParticipantStart(participant)
            val resultPart = ParticipantResult(
                participantStart,
                it["Порядковый номер"]?.toIntOrNull() ?: throw CSVFieldNamesException(protocol.path),
                it["Место"]?.toIntOrNull() ?: -1,
                it["Отставание"] ?: throw CSVFieldNamesException(protocol.path)
            )
            groupsMap[nameOfGroup]?.add(resultPart)
        }
    }

}