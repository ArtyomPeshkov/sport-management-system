package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import log.universalC
import java.io.File

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
    parseLogger.universalC(
        Colors.RED._name,
        "ATTENTION: BLACK MAGIC HAPPENS. WE ARE GENERATING CONTROL POINTS. EVERYTHING CAN GO WRONG AT ANY MOMENT. ADVISE YOU TO PREPARE FOR THE WORST",
        'i'
    )
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