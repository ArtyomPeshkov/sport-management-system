package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

val parseLogger: Logger = LoggerFactory.getLogger("Parse")

fun readFile(path: String): File {
    parseLogger.universalC(Colors.YELLOW._name, "Reading file: $path")
    try {
        return File(path)
    } catch (e: Exception) {
        throw ProblemWithFilePathException(path)
    }
}

fun chooseSex(sex: String): Sex {
    return when (sex) {
        "М", "M", "m", "м" -> Sex.MALE
        "Ж", "F", "ж", "f" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}

fun distancesParser(distances: File): Map<String, Distance> {
    val distanceStrings = csvReader().readAllWithHeader(distances)
    val controlPoints = hashSetOf<ControlPoint>()
    return distanceStrings.associate {
        Pair(
            it["Название"] ?: throw CSVFieldNamesException(distances.path),
            Distance(it, distances.path, controlPoints)
        )
    }
}

fun groupsParser(distanceList: Map<String, Distance>, groups: File, currentPhase: Phase): List<Group> {
    val groupStrings = csvReader().readAllWithHeader(groups)
    return groupStrings.map { group ->
        val distance = distanceList[group["Дистанция"]] ?: throw CSVFieldNamesException(groups.path)
        parseLogger.universalC(Colors.BLUE._name, distance.toString())
        if (currentPhase == Phase.FIRST) {
            Group(group, distance, groups.path)
        } else
            Group(group["Название"] ?: throw CSVFieldNamesException(groups.path), distance)
    }.toSet().toList()
}

fun collectivesParser(applicationsFolder: File): List<Collective> {
    val applications =
        applicationsFolder.walk().toList().filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
    return applications.map { Collective(it.path) }
}

fun getCollectives(configurationFolder: List<File>, path: String) =
    collectivesParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
        ?: throw NotEnoughConfigurationFiles(path))

fun getDistances(configurationFolder: List<File>, path: String) =
    distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
        ?: throw NotEnoughConfigurationFiles(path))

fun getGroups(configurationFolder: List<File>, distanceList: Map<String, Distance>, path: String, currentPhase: Phase) =
    groupsParser(distanceList, configurationFolder.find { it.name.substringAfterLast('/') == "groups.csv" }
        ?: throw NotEnoughConfigurationFiles(path), currentPhase)

data class nameDate(val name: String, val date: LocalDate)

fun getNameAndDate(configurationFolder: List<File>, path: String): nameDate {
    val rows = csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
        ?: throw NotEnoughConfigurationFiles(path))
    return if (rows.size != 1)
        throw ProblemWithCSVException(path)
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        nameDate(
            rows[0]["Название"] ?: throw CSVStringWithNameException(path),
            LocalDate.parse(rows[0]["Дата"], formatter)
        )
    } else
        throw CSVStringWithNameException(path)
}

fun phase1(path: String) {
    val configurationFolder = readFile(path).walk().toList()
    val distances = getDistances(configurationFolder, path)
    val groups = getGroups(configurationFolder, distances, path, Phase.FIRST)
    val collective = getCollectives(configurationFolder, path)
    val (name, date) = getNameAndDate(configurationFolder, path)
    val event = Event(name, date, groups, distances, collective)
    println(event.toString())
}

fun parseStartProtocolFiles(startsFolder: File, groups: List<Group>) {
    val startInfo =
        startsFolder.walk().toList().filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
    startInfo.map { parseStartProtocol(it, groups) }
}

fun getStartProtocolFolder(configurationFolder: List<File>, path: String, groups: List<Group>) =
    parseStartProtocolFiles(configurationFolder.find { it.name.substringAfterLast('/') == "starts" }
        ?: throw NotEnoughConfigurationFiles(path), groups)

fun parseStartProtocol(protocol: File, groups: List<Group>) {
    val fileStrings = csvReader().readAll(protocol.readText().substringBefore("\n"))
    val nameOfGroup = fileStrings[0].let {
        if (it.size != 7)
            throw CSVStringWithNameException(protocol.path)
        else it[0]
    }
    val indexOfGroup = getGroupIndexByName(nameOfGroup, groups)
    //Тут будет Map вместо List
    val participantData = csvReader().readAll(protocol).drop(1)
    participantData.forEach {
        val participant = Participant(
            nameOfGroup, /*TODO("пол должен передаваться вместе с участником")*/
            chooseSex(nameOfGroup[0].toString()),
            it[1],
            it[2],
            it[3].toInt(),
            it[5]
        )
        participant.setCollective(it[4])
        participant.setStart(it[0].toInt(), Time(it[6]))
        groups[indexOfGroup].addParticipant(participant)

    }
}


fun getGroupByName(name: String, groups: List<Group>): Group? {
    return groups.find { it.groupName == name }
}

fun getGroupIndexByName(name: String, groups: List<Group>): Int {
    val group = getGroupByName(name, groups)
    return if (group != null) {
        groups.indexOf(group)
    } else throw UnexpectedValueException(group)
}

fun parseCPFiles(pointsFolder: File): Map<Int, List<ControlPointWithTime>> {
    val res: MutableList<Pair<Int, ControlPointWithTime>> = mutableListOf()
    val pointsInfo =
        pointsFolder.walk().toList().filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
    pointsInfo.forEach { res += parseCP(it) }
    return res.groupBy({ it.first }, { it.second })
}

fun getCPFolder(configurationFolder: List<File>, path: String): Map<Int, List<ControlPointWithTime>> =
    parseCPFiles(configurationFolder.find { it.name.substringAfterLast('/') == "points" }
        ?: throw NotEnoughConfigurationFiles(path))


fun parseCP(protocol: File): List<Pair<Int, ControlPointWithTime>> {
    val fileFirstString = csvReader().readAll(protocol.readText().substringBefore("\n"))
    val nameOfControlPoint = fileFirstString[0].let {
        if (it.size != 2)
            throw CSVStringWithNameException(protocol.path)
        else it[0]
    }
    val eachControlPoint = csvReader().readAll(protocol).drop(1)
    return eachControlPoint.map { Pair(it[0].toInt(), ControlPointWithTime(nameOfControlPoint, Time(it[1]))) }

}

fun checkProtocolPointsCorrectness(
    participantNumber: Int,
    distances: Distance,
    participantDistance: Map<Int, List<ControlPointWithTime>>
): String {
   if (distances.getPointsList() != participantDistance[participantNumber]?.map{it.name})
       return "Снят"
   var previousCP = ControlPointWithTime("Prev", Time(0))
   participantDistance[participantNumber]?.forEach {
        if (it.time<=previousCP.time)
            return "Снят"
        previousCP=it
   }
   return previousCP.time.toString()
}

fun phase2(path: String) {
    val configurationFolder = readFile(path).walk().toList()
    parseLogger.printCollection(configurationFolder, Colors.GREEN._name)
    val distances = getDistances(configurationFolder, path)
    parseLogger.printMap(distances, Colors.BLUE._name)
    val groups = getGroups(configurationFolder, distances, path, Phase.SECOND)
    getStartProtocolFolder(configurationFolder, path, groups)
    val participantDistance: Map<Int, List<ControlPointWithTime>> = getCPFolder(configurationFolder, path)
    parseLogger.universalC(Colors.RED._name, "${participantDistance.size}", 'd')
    parseLogger.printMap(participantDistance, Colors.YELLOW._name)
    groups.forEach { group ->
        group.listParticipants.forEach { participant ->
            participant.setParticipantStatus(checkProtocolPointsCorrectness(participant.number, distances[group.distance.name] ?: throw UnexpectedValueException(group.distance.name), participantDistance))
        }
    }
    parseLogger.printCollection(groups, Colors.PURPLE._name)
}


fun main(args: Array<String>) {
    val path = "csvFiles/configuration"
    phase2(path)
}

