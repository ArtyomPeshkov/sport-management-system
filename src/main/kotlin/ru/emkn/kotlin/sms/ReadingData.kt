package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.universalC
import java.io.File

abstract class Reader(configurationFolderPath: String){
    private val configurationFolder: List<File>
    init{
        this.configurationFolder = File(configurationFolderPath).walk().toList()
    }
    fun universalParser(name: String): List<File> {
        val folder = configurationFolder.find { it.name.substringAfterLast('/') == name }
            ?: throw NotEnoughConfigurationFiles(name)
        parseLogger.universalC(Colors.BLUE._name, "reading from  ${configurationFolder[0]}\\$name", 'i')
        return folder.walk().toList().filter { it.extension == "csv" }
    }
}

class TeamReader(configurationFolderName:String):Reader(configurationFolderName)
{
    fun getTeams():List<Team> = universalParser("applications").map { getParticularTeam(readFile(it.path)) }
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

class ControlPointReader(configurationFolderName: String):Reader(configurationFolderName)
{
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
        return eachControlPoint.map { Pair(it[0].toInt(), ControlPointWithTime(ControlPoint(nameOfControlPoint), Time(it[1]))) }
    }
}

class DistanceReader(configurationFolderName: String):Reader(configurationFolderName)
{
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

class GroupReader(configurationFolderName:String):Reader(configurationFolderName)
{
    fun getGroups(distanceList: Map<String, Distance>, currentPhase: Phase):List<Group> {
        val groups =universalParser("groups.csv")[0]
        val groupStrings = csvReader().readAllWithHeader(groups)
        return groupStrings.map { groupData ->
            val groupName = groupData["Название"] ?: throw CSVFieldNamesException(groups.path)
            val distance = distanceList[groupData["Дистанция"]] ?: throw CSVFieldNamesException(groups.path)

            val group = Group(groupName, distance)
            if (currentPhase == Phase.FIRST) {
                group.modifyGroup(groupData, groups.path)
            }

            group
        }.toSet().toList()
    }
}

class ResultsReader(configurationFolderName:String):Reader(configurationFolderName)
{

    fun getGroupsFromResultProtocols():MutableList<Team> {
        val resultsFolder = universalParser("results")
        val teams = mutableListOf<Team>()
        resultsFolder.forEach{ parseResultProtocol(it,teams) }
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
        } catch(e: IllegalTimeFormatException) {
            Time(0)
        }

        val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
        participantData.forEachIndexed {i,it ->
            val participant = makeParticipant(it,i,protocol.path,"",nameOfGroup)
            val currentTime = it["Результат"] ?: throw CSVFieldNamesException(protocol.path)
            try {
                participant.setPoints(
                    Integer.max(
                        0,
                        (100.0 * (2 - Time(currentTime).timeInSeconds.toDouble() / bestResult.timeInSeconds)).toInt()
                    )
                )
            } catch(e: IllegalTimeFormatException) {
                participant.setParticipantStatus("Снят")
                participant.setPoints(0)
            }
            val collectiveName = it["Коллектив"] ?: throw CSVFieldNamesException(protocol.path)
            if (teams.find { it.name == collectiveName } == null) teams.add(Team(collectiveName))
            val collective =
                teams.find { it.name == collectiveName } ?: throw UnexpectedValueException(collectiveName)
            collective.addParticipant(participant)
        }
    }
}

class StartProtocolParse(configurationFolderName: String):Reader(configurationFolderName)
{
    fun getStartProtocolFolder( groups: List<Group>) {
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
        participantData.forEachIndexed {i,it->
            val participant = makeParticipant(it,i,protocol.path,"",nameOfGroup)
            participant.setStart(
                it["Номер"]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
                Time(it["Стартовое время"] ?: throw CSVFieldNamesException(protocol.path))
            )
            groups[indexOfGroup].addParticipant(participant)

        }
    }

}