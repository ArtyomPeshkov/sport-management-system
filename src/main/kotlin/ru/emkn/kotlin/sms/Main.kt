package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.*
import log.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.Integer.max

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
    parseLogger.universalC(
        Colors.YELLOW._name,
        "you are using phase 1: form start protocols according to the application lists",
        'i'
    )
    //Лог уровня дебаг, информация о содержимом полученных коллекций и переменных
    val controlPoints = mutableSetOf<ControlPoint>()
    val distances = DistanceReader(path).getDistances(controlPoints)

    val groups = GroupReader(path).getGroups(distances, Phase.FIRST)
    val team = TeamReader(path).getTeams()
    val (name, date) = getNameAndDate(readFile(path).walk().toList(), path)

    val event = Event(name, date, groups, distances, team)

    event.getDistanceList().forEach {
        event.setNumbersAndTime(event.getGroupsByDistance(it.value))
    }
    event.makeStartProtocols()

    generateCP(controlPoints, groups)
    parseLogger.universalC(Colors.PURPLE._name, "some info about event: $event", 'i')
}

fun phase2(path: String) {
    parseLogger.universalC(
        Colors.YELLOW._name,
        "you are using phase 2: form result protocols according to start protocols and protocols of passing control points",
        'i'
    )
    val configurationFolder = readFile(path).walk().toList()
    parseLogger.printCollection(configurationFolder, Colors.GREEN._name)
    val distances =  DistanceReader(path).getDistances()
    parseLogger.printMap(distances, Colors.BLUE._name)
    val groups = GroupReader(path).getGroups(distances, Phase.SECOND)
    StartProtocolParse(path).getStartProtocolFolder(groups)
    val participantDistanceWithTime: Map<Int, List<ControlPointWithTime>> = ControlPointReader(path).getPoints()
    parseLogger.universalC(Colors.RED._name, "${participantDistanceWithTime.size}", 'd')
    parseLogger.printMap(participantDistanceWithTime, Colors.YELLOW._name)
    setStatusForAllParticipants(groups, distances, participantDistanceWithTime)
    parseLogger.printCollection(groups, Colors.PURPLE._name)
    makeResultProtocols(groups)
}

fun phase3(path: String) {
    val teams = ResultsReader(path).getGroupsFromResultProtocols()
    generateResultProtocolForCollectives(teams)
}

fun main(args: Array<String>) {
    parseLogger.universalC(
        Colors.GREEN._name,
        "Hello, dear programmer. At your own peril and risk, you decided to work with this program.\nWell, this is very brave of you and very pleasant for us - creators.\nWe hope you will be satisfied with the work and everything will go according your plan.\nSo, let's begin our hunger games. Good luck))",
        'i'
    )
    val path = "csvFiles/configuration"
    phase1(path)
    phase2(path)
    phase3(path)
    parseLogger.universalC(
        Colors.GREEN._name,
        "Well, everything has gone according to your plan. You are very lucky, right?\nThank you for using our programm. Have a good day. Goodbye",
        'i'
    )

}

