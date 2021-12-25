package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.UnexpectedValueException
import log.universalC
import java.io.File


fun generateCP(controlPoints: List<ControlPoint>, groups: List<Group>, configurationFolder:String) {
    parseLogger.universalC(
        Colors.RED._name,
        "ATTENTION: BLACK MAGIC HAPPENS. WE ARE GENERATING CONTROL POINTS. EVERYTHING CAN GO WRONG AT ANY MOMENT. ADVISE YOU TO PREPARE FOR THE WORST",
        'i'
    )
    val generationDir = "$configurationFolder/points/"
    controlPoints.forEach { controlPoint ->
        File(generationDir).mkdirs()
        val file = File("${generationDir}control-point_${controlPoint.name}.csv")
        csvWriter().writeAll(listOf(listOf(controlPoint.name, "")), file, append = false)
    }
        groups.forEach { group ->
            group.listParticipants.forEach { participant ->
                val pointList = group.distance.getPointsList().toMutableList()
                var lastTime = participant.startTime
                pointList.forEach {
                    val newTime = lastTime + Time((10..20).random())
                    lastTime = newTime
                    csvWriter().writeAll(listOf(listOf(participant.number.toString(),newTime.toString())), File("${generationDir}control-point_${it.name}.csv"), append = true)
                }


               /* if (pointList.contains(controlPoint)) {
                    val curIndex = pointList.indexOf(controlPoint)
                    if (curIndex == 0) {
                        val newTime = (controlPointMap[Pair(startPoint, participant)] ?: throw UnexpectedValueException(
                            "Participant: $participant does not have control point: $startPoint"
                        )) + Time((10..20).random())
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
                }*/
            }
        }

}