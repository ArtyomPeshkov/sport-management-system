package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import exceptions.UnexpectedValueException
import log.universalC
import java.io.File

fun getCPFolder(configurationFolder: List<File>, path: String): Map<Int, List<ControlPointWithTime>> =
    parseCPFiles(configurationFolder.find { it.name.substringAfterLast('/') == "points" }
        ?: throw NotEnoughConfigurationFiles(path))

fun parseCPFiles(pointsFolder: File): Map<Int, List<ControlPointWithTime>> {
    parseLogger.universalC(
        Colors.BLUE._name,
        "reading control points and how participants passed them from folder ${pointsFolder.path}",
        'i'
    )
    val res: MutableList<Pair<Int, ControlPointWithTime>> = mutableListOf()
    val pointsInfo =
        pointsFolder.walk(FileWalkDirection.TOP_DOWN)
            .filter { it.extension == "csv" }
    pointsInfo.forEach { res += parseCP(it) }
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
    return eachControlPoint.map { Pair(it[0].toInt(), ControlPointWithTime(nameOfControlPoint, Time(it[1]))) }

}

fun checkProtocolPointsCorrectness(
    participant: Participant,
    distances: Distance,
    participantDistance: Map<Int, List<ControlPointWithTime>>
): String {
    val controlPoints = distances.getPointsList()
    val participantControlPoints = participantDistance[participant.number]
    //Если в список попала несуществующая КТ, участник снимается, не знаю, насколько это правильно
    if (!controlPoints.containsAll(participantControlPoints?.map { ControlPoint(it.name) } ?: listOf(
            ControlPoint("")
        )))
        return "Снят"
    val first = ControlPointWithTime("Start", participant.startTime)
    val lastControlPointTime =
        participantControlPoints?.find { it.name == controlPoints.last().name }?.time ?: return "Снят"
    participantControlPoints.forEach {
        val previousControlPointIndex = controlPoints.indexOf(ControlPoint(it.name))
        val previousControlPointWithTime = if (previousControlPointIndex != 0) {
            val previousControlPoint = controlPoints[previousControlPointIndex - 1]
            participantControlPoints.find { previousControlPoint.name == it.name }
                ?: throw UnexpectedValueException(previousControlPoint.name)
        } else
            first
        if (it.time <= previousControlPointWithTime.time)
            return "Снят"
    }
    return (lastControlPointTime - first.time).toString()
}
