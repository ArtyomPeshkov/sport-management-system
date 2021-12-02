package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.*
import log.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate

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

fun distancesParser(distances: File, controlPoints: MutableSet<ControlPoint>): Map<String, Distance> {
    val distanceStrings = csvReader().readAllWithHeader(distances)
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

fun getDistances(
    configurationFolder: List<File>,
    path: String,
    controlPoints: MutableSet<ControlPoint> = mutableSetOf()
) =
    distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
        ?: throw NotEnoughConfigurationFiles(path), controlPoints)

fun getGroups(configurationFolder: List<File>, distanceList: Map<String, Distance>, path: String, currentPhase: Phase) =
    groupsParser(distanceList, configurationFolder.find { it.name.substringAfterLast('/') == "groups.csv" }
        ?: throw NotEnoughConfigurationFiles(path), currentPhase)

data class NameDate(val name: String, val date: LocalDate)

fun getNameAndDate(configurationFolder: List<File>, path: String): NameDate {
    val rows = csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
        ?: throw NotEnoughConfigurationFiles(path))
    return if (rows.size != 1)
        throw ProblemWithCSVException(path)
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        NameDate(
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
        if (it[0] == "")
            throw CSVStringWithNameException(protocol.path)
        else it[0]
    }
    val indexOfGroup = getGroupIndexByName(nameOfGroup, groups)
    val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
    participantData.forEach {
        val participant = Participant(
            nameOfGroup, /*TODO("пол должен передаваться вместе с участником")*/
            chooseSex(nameOfGroup[0].toString()),
            it["Фамилия"] ?: throw CSVFieldNamesException(protocol.path),
            it["Имя"] ?: throw CSVFieldNamesException(protocol.path),
            it["Г.р."]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
            it["Разр."] ?: throw CSVFieldNamesException(protocol.path)
        )
        participant.setCollective(it["Коллектив"] ?: throw CSVFieldNamesException(protocol.path))
        participant.setStart(
            it["Номер"]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
            Time(it["Стартовое время"] ?: throw CSVFieldNamesException(protocol.path))
        )
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
        pointsFolder.walk(FileWalkDirection.TOP_DOWN)
            .filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
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
    participant: Participant,
    distances: Distance,
    participantDistance: Map<Int, List<ControlPointWithTime>>
): String {
    if (participantDistance[participant.number]?.map { ControlPoint(it.name) }
            ?.containsAll(distances.getPointsList()) == false)
        return "Снят"
    var previousCP = ControlPointWithTime("Prev", Time(0))
    participantDistance[participant.number]?.forEach {
        if (it.time <= previousCP.time)
            return "Снят"
        previousCP = it
    }
    return (previousCP.time-participant.startTime).toString()
}

fun makeResultProtocols(groups: List<Group>) {
    val resultDir = File("csvFiles/configuration/results/")
    resultDir.mkdirs()
    //val generalFile = File("csvFiles/starts/start_general.csv")
    //val generalLines = mutableListOf<List<String>>()
    groups.filter { it.listParticipants.size > 0 }.forEach { group ->
        val resultGroupFile = File("${resultDir.path}/result_${group.groupName}.csv")
        val helper = group.listParticipants[0]
        csvWriter().writeAll(
            listOf(
                List(helper.headerFormatCSVResult().size) { if (it == 0) group.groupName else "" },
                helper.headerFormatCSVResult()
            ), resultGroupFile, append = false
        )
        val (participants, deletedParticipants) = group.listParticipants.partition { it.status != "Снят" }
        val result: List<Participant> = participants.sortedBy { Time(it.status).timeInSeconds } + deletedParticipants
        var number = 1
        var place = 1
        csvWriter().writeAll(result.map {
            listOf(
                number++.toString(),
                it.number,
                it.surname,
                it.name,
                it.sex,
                it.yearOfBirth,
                it.collective,
                it.rank,
                it.status,
                if (number!= 2 && result[number-3].status==result[number-2].status && result[number-2].status != "Снят") place-1 else if (result[number-2].status!= "Снят") place++ else "",
                if (place!=2 && it.status != "Снят") Time(it.status)-Time(result[0].status) else ""
            )
        }, resultGroupFile, append = true)
        // generalLines.addAll(group.listParticipants.map { it.toCSV() })
    }
    //  csvWriter().writeAll(generalLines, generalFile)
}

fun phase2(path: String) {
    val configurationFolder = readFile(path).walk().toList()
    parseLogger.printCollection(configurationFolder, Colors.GREEN._name)
    val controlPoints = mutableSetOf<ControlPoint>()
    val distances = getDistances(configurationFolder, path, controlPoints)
    parseLogger.printMap(distances, Colors.BLUE._name)
    val groups = getGroups(configurationFolder, distances, path, Phase.SECOND)
    getStartProtocolFolder(configurationFolder, path, groups)
    startCP(groups)
    generateCP(controlPoints, groups)
    val participantDistanceWithTime: Map<Int, List<ControlPointWithTime>> = getCPFolder(configurationFolder, path)
    parseLogger.universalC(Colors.RED._name, "${participantDistanceWithTime.size}", 'd')
    parseLogger.printMap(participantDistanceWithTime, Colors.YELLOW._name)
    groups.forEach { group ->
        group.listParticipants.forEach { participant ->
            participant.setParticipantStatus(
                checkProtocolPointsCorrectness(
                    participant,
                    distances[group.distance.name] ?: throw UnexpectedValueException(group.distance.name),
                    participantDistanceWithTime
                )
            )
        }
    }
    parseLogger.printCollection(groups, Colors.PURPLE._name)
    makeResultProtocols(groups)
}

fun startCP(groupList: List<Group>) {
    val generalFile = File("csvFiles/configuration/points/cp00.csv")
    csvWriter().writeAll(listOf(listOf("Номер", "Время старта")), generalFile, append = false)
    groupList.filter { it.listParticipants.size > 0 }.forEach { group ->
        csvWriter().writeAll(group.listParticipants.map { it.toCSVStartTime() }, generalFile, append = true)
    }
}

//РАБОТАЕТ НА ЧЕСТНОМ СЛОВЕ И УЛИЧНОЙ МАГИИ
//Если серьёзно, эта штука очень чувствительна к порядку файлов в папке, поэтому будем требовать, чтобы все контрольные точки были отсортированы в том порядке, как они идут в дистанциях
//Для условного спортивного ориентирования придётся менять концепцию генерации и не обращаться к более старым файлам, а заводить массив, хранящий для каждого участника предыдущую КТ
//и время её прохождения, чтобы генерировать новые. Но это решает сразу все проблемы.
fun generateCP(controlPoints: Set<ControlPoint>, groups: List<Group>) {
    var num = 1;
    controlPoints.forEach { controlPoint ->
        val prev = "${num - 1}".padStart(controlPoints.size.toString().length, '0')
        val cur = "${num++}".padStart(controlPoints.size.toString().length, '0')
        val fileFrom = csvReader().readAll(File("csvFiles/configuration/points/cp$prev.csv"))
        val file = File("csvFiles/configuration/points/cp$cur.csv")
        csvWriter().writeAll(listOf(listOf(controlPoint.name, "")), file, append = false)
        val listOfControlPoints = mutableListOf<ControlPointWithTime>()
        fileFrom.drop(1).forEach { fileStr ->
            if (groups.any {
                    it.listParticipants.map { it.number }.contains(fileStr[0].toInt()) && it.distance.getPointsList()
                        .contains(controlPoint)
                }
            ) listOfControlPoints += ControlPointWithTime(
                fileStr[0],
                Time(fileStr[1]).plus(Time((10..20).random()))
            )
        }
        csvWriter().writeAll(listOfControlPoints.map { it.toCSV() }, file, append = true)
    }
    File("csvFiles/configuration/points/cp00.csv").delete()
}


fun main(args: Array<String>) {
    val path = "csvFiles/configuration"
    phase2(path)
}

