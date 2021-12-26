package ru.emkn.kotlin.sms

import exceptions.*
import log.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val parseLogger: Logger = LoggerFactory.getLogger("Parse")

fun newFileReader(): String {
    println("Write path to your config folder")
    var resultFile: String? = readLine()
    while (resultFile == null || !File(resultFile).exists() || !File(resultFile).canRead() ) {
        println("Please write existing file")
        resultFile = readLine()
    }
    return resultFile
}

fun simpleAnswerForQuestion(): Boolean {
    var ans = readLine()
    while (true) {
        when (ans) {
            "Y", "y", "Yes", "yes", "YES" -> return true
            "N", "n", "No", "no", "NO" -> return false
            else -> println("Please write 'y' or 'n'")
        }
        ans = readLine()
    }
}

/** функция считывающая файл */
fun readFile(path: String): File {
    parseLogger.universalC(Colors.YELLOW._name, "Reading file: $path")
    try {
        return File(path)
    } catch (e: Exception) {
        throw ProblemWithFilePathException(path)
    }
}

/** возвращает гендер (в файлах допускается использование разного написания названия гендеров) */
fun chooseGender(gender: String) = when (gender) {
        "М", "M", "m", "м", "MALE" -> Sex.MALE
        "Ж", "F", "ж", "f", "FEMALE" -> Sex.FEMALE
        else -> throw SexException(gender)
    }

/** запускает фазу 1 работы программы */
fun phase1(path: String) {
    parseLogger.universalC(
        Colors.YELLOW._name,
        "you are using phase 1: form start protocols according to the application lists",
        'i'
    )
    //Лог уровня дебаг, информация о содержимом полученных коллекций и переменных
    val controlPoints = mutableListOf<ControlPoint>()
    val distances = DistanceReader(path).getDistances(controlPoints)

    val groups = GroupReader(path).getGroups(distances)
    val team = TeamReader(path).getTeams()
    val (name, date) = getNameAndDate(readFile(path).walk().toList(), path)

    val event = Event(name, date, groups, distances, team)

    event.getDistanceList().forEach {
        event.setNumbersAndTime(event.getGroupsByDistance(it.value))
    }
    event.makeStartProtocols(path)
    println("Do you want to generate control points randomly?(y/n)")
    if (simpleAnswerForQuestion()) {
        generateCP(controlPoints, groups, path)
    }
    parseLogger.universalC(Colors.PURPLE._name, "some info about event: $event", 'i')
}

/** запускает фазу 2 работы программы */
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
    val groups = GroupReader(path).getGroups(distances)
    StartProtocolParse(path).getStartProtocolFolder(groups)
    val participantDistanceWithTime: Map<Int, List<ControlPointWithTime>> = ControlPointReader(path).getPoints()
    parseLogger.universalC(Colors.RED._name, "${participantDistanceWithTime.size}", 'd')
    parseLogger.printMap(participantDistanceWithTime, Colors.YELLOW._name)
    setStatusForAllParticipants(groups, distances, participantDistanceWithTime)
    parseLogger.printCollection(groups, Colors.PURPLE._name)
    makeResultProtocols(groups,path)
}

/** запускает фазу 3 работы программы */
fun phase3(path: String) {
    val teams = ResultsReader(path).getGroupsFromResultProtocols()
    generateResultProtocolForCollectives(teams,path)
}

fun main(args: Array<String>) {
    parseLogger.universalC(
        Colors.GREEN._name,
        "Hello, dear programmer. At your own peril and risk, you decided to work with this program.\nWell, this is very brave of you and very pleasant for us - creators.\nWe hope you will be satisfied with the work and everything will go according your plan.\nSo, let's begin our hunger games. Good luck))",
        'i'
    )
    val path = newFileReader()
    phase1(path)
    phase2(path)
    phase3(path)
    parseLogger.universalC(
        Colors.GREEN._name,
        "Well, everything has gone according to your plan. You are very lucky, right?\nThank you for using our programm. Have a good day. Goodbye",
        'i'
    )

}

