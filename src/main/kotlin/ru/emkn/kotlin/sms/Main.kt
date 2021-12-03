package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.*
import log.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Integer.max
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
        "М", "M", "m", "м", "MALE" -> Sex.MALE
        "Ж", "F", "ж", "f", "FEMALE" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}

fun phase1(path: String) {
    parseLogger.universalC(Colors.YELLOW._name, "you are using phase 1: form start protocols according to the application lists", 'i')
    val configurationFolder = readFile(path).walk().toList()

    val controlPoints = mutableSetOf<ControlPoint>()
    val distances = getDistances(configurationFolder, path,controlPoints)

    val groups = getGroups(configurationFolder, distances, path, Phase.FIRST)
    val collective = getCollectives(configurationFolder, path)
    val (name, date) = getNameAndDate(configurationFolder, path)

    val event = Event(name, date, groups, distances,collective)

    event.getDistanceList().forEach {
        event.setNumbersAndTime(event.getGroupsByDistance(it.value))
    }
    event.makeStartProtocols()

    generateCP(controlPoints, groups)
    parseLogger.universalC(Colors.PURPLE._name, "some info about event: $event", 'i')
}

fun phase2(path: String) {
    parseLogger.universalC(Colors.YELLOW._name, "you are using phase 2: form result protocols according to start protocols and protocols of passing control points", 'i')
    val configurationFolder = readFile(path).walk().toList()
    parseLogger.printCollection(configurationFolder, Colors.GREEN._name)
    val controlPoints = mutableSetOf<ControlPoint>()
    val distances = getDistances(configurationFolder, path, controlPoints)
    parseLogger.printMap(distances, Colors.BLUE._name)
    val groups = getGroups(configurationFolder, distances, path, Phase.SECOND)
    getStartProtocolFolder(configurationFolder, path, groups)
    val participantDistanceWithTime: Map<Int, List<ControlPointWithTime>> = getCPFolder(configurationFolder, path)
    parseLogger.universalC(Colors.RED._name, "${participantDistanceWithTime.size}", 'd')
    parseLogger.printMap(participantDistanceWithTime, Colors.YELLOW._name)
    setStatusForAllParticipants(groups,distances,participantDistanceWithTime)
    parseLogger.printCollection(groups, Colors.PURPLE._name)
    makeResultProtocols(groups)
}

fun startCP(groupList: List<Group>): Pair<ControlPoint, MutableMap<Pair<ControlPoint, Participant>, Time>> {
    val resultsOfParticipantsAtParticularPoint: MutableMap<Pair<ControlPoint, Participant>, Time> = mutableMapOf()
    val start = ControlPoint("Start")
    groupList.forEach { group ->
        group.listParticipants.forEach {
            resultsOfParticipantsAtParticularPoint[Pair(start, it)] = (it.startTime)
        }
    }
    return Pair(start, resultsOfParticipantsAtParticularPoint)
}

data class ParticipantTimeAtControlPoint(val number: String, val time: String)

fun generateCP(controlPoints: Set<ControlPoint>, groups: List<Group>) {
    parseLogger.universalC(Colors.RED._name, "ATTENTION: BLACK MAGIC HAPPENS. WE ARE GENERATING CONTROL POINTS. EVERYTHING CAN GO WRONG AT ANY MOMENT. ADVISE YOU TO PREPARE FOR THE WORST", 'i')
    val (startPoint, controlPointMap) = startCP(groups)
    controlPoints.forEach { controlPoint ->
        val listOfControlPoints = mutableListOf<ParticipantTimeAtControlPoint>()
        val generationDir = "csvFiles/configuration/points/"
        File(generationDir).mkdirs()
        val file = File("${generationDir}control-point_${controlPoint.name}.csv")
        csvWriter().writeAll(listOf(listOf(controlPoint.name, "")), file, append = false)
        groups.forEach { group ->
            group.listParticipants.forEach { participant ->
                val pointList = group.distance.getPointsList()
                if (pointList.contains(controlPoint)) {
                    val curIndex = pointList.indexOf(controlPoint)
                    if (curIndex == 0) {
                        val newTime = controlPointMap[Pair(startPoint, participant)]!! + Time((10..20).random())
                        listOfControlPoints += ParticipantTimeAtControlPoint(
                            participant.number.toString(),
                            newTime.toString()
                        )
                        controlPointMap[Pair(controlPoint, participant)] = newTime
                    } else {
                        val previousPoint = pointList[curIndex - 1]
                        val newTime = controlPointMap[Pair(previousPoint, participant)]!! + Time((10..20).random())
                        listOfControlPoints += ParticipantTimeAtControlPoint(
                            participant.number.toString(),
                            newTime.toString()
                        )
                        controlPointMap[Pair(controlPoint, participant)] = newTime
                    }
                }
            }
        }
        csvWriter().writeAll(listOfControlPoints.map { listOf(it.number, it.time) }, file, append = true)
    }
}

fun phase3(path: String) {
    val configurationFolder = readFile(path).walk().toList()
    val collectives = mutableListOf<Collective>()
    getResultProtocolFolder(configurationFolder, path, collectives)
    generateResultProtocolForCollectives(collectives)
}

fun getResultProtocolFolder(configurationFolder: List<File>, path: String, collectives: MutableList<Collective>) =
    getGroupsFromResultProtocols(configurationFolder.find { it.name.substringAfterLast('/') == "results" }
        ?: throw NotEnoughConfigurationFiles(path), collectives)

fun getGroupsFromResultProtocols(resultsFolder: File, collectives: MutableList<Collective>) {
    val resultInfo =
        resultsFolder.walk().toList().filter { it.extension=="csv" }
    resultInfo.map { parseResultProtocol(it, collectives) }
}
fun parseResultProtocol(protocol: File, collectives: MutableList<Collective>) {
    val fileStrings = csvReader().readAll(protocol.readText())
    val nameOfGroup = fileStrings[0].let {
        if (it[0] == "")
            throw CSVStringWithNameException(protocol.path)
        else it[0]
    }
    val bestResult = Time(fileStrings[2][8]) ?: throw CSVFieldNamesException(protocol.path)
    val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
    participantData.forEach {
        val participant = Participant(
            nameOfGroup,
            chooseSex(it["Пол"] ?: throw CSVFieldNamesException(protocol.path)),
            it["Фамилия"] ?: throw CSVFieldNamesException(protocol.path),
            it["Имя"] ?: throw CSVFieldNamesException(protocol.path),
            it["Г.р."]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
            it["Разр."] ?: throw CSVFieldNamesException(protocol.path)
        )
        val currentTime = it["Результат"] ?: throw CSVFieldNamesException(protocol.path)
        participant.setPoints(max(0, (100.0 * (2 - Time(currentTime).timeInSeconds.toDouble()/bestResult.timeInSeconds)).toInt()))
        val collectiveName = it["Коллектив"] ?: throw CSVFieldNamesException(protocol.path)
        if (collectives.find { it.name == collectiveName } == null) collectives.add(Collective(collectiveName))
        val collective = collectives.find { it.name == collectiveName } ?: throw UnexpectedValueException(collectiveName)
        collective.addParticipant(participant)
    }
}

fun generateResultProtocolForCollectives(collectives: MutableList<Collective>) {
    val file = File("csvFiles/configuration/collectivesResults.csv")
    file.writeText("")
    collectives.forEach {
        csvWriter().writeAll(listOf(listOf("Коллектив", "Баллы"),listOf(it.name,"${it.points}"),listOf(""),listOf("Участник", "Баллы")), file, append = true)
        it.athleteList.forEach {
            csvWriter().writeAll(
                listOf(listOf("${it.surname} ${it.name}", "${it.points}")),
                file,
                append = true
            )
        }
        csvWriter().writeAll(listOf(listOf(""),listOf("<-------------------------------------------------------->"),listOf("")), file, append = true)
    }
}

fun main(args: Array<String>) {
    parseLogger.universalC(Colors.GREEN._name, "Hello, dear programmer. At your own peril and risk, you decided to work with this program.\nWell, this is very brave of you and very pleasant for us - creators.\nWe hope you will be satisfied with the work and everything will go according your plan.\nSo, let's begin our hunger games. Good luck))", 'i')
    val path = "csvFiles/configuration"
    phase1(path)
    phase2(path)
    phase3(path)
    parseLogger.universalC(Colors.GREEN._name, "Well, everything has gone according to your plan. You are very lucky, right?\nThank you for using our programm. Have a good day. Goodbye", 'i')
}

