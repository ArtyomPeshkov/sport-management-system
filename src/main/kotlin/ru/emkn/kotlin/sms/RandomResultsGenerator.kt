package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.UnexpectedValueException
import log.universalC
import java.io.File


/**
 * функция создающая файлы с результатами прохождения каждой контрольной точки,
 * рандомно сгенерированными для каждого участника
 */
fun generateCP(controlPoints: List<ControlPoint>, groups: List<Group>, configurationFolder:String) {
    parseLogger.universalC(
        Colors.RED._name,
        "ATTENTION: BLACK MAGIC HAPPENS. WE ARE GENERATING CONTROL POINTS. EVERYTHING CAN GO WRONG AT ANY MOMENT. ADVISE YOU TO PREPARE FOR THE WORST",
        'i'
    )
    val generationDir = File("$configurationFolder/points/")
    generationDir.deleteRecursively()
    generationDir.mkdirs()
    controlPoints.forEach { controlPoint ->
        val file = File("${generationDir.path}/control-point_${controlPoint.name}.csv")
        csvWriter().writeAll(listOf(listOf(controlPoint.name, "")), file, append = false)
    }
    groups.forEach { group ->
        group.listParticipants.forEach { participant ->
            val pointList = group.distance.getPointsList().toMutableList()
            var lastTime = participant.startTime
            pointList.forEach {
                val newTime = lastTime + Time((10..20).random())
                lastTime = newTime
                csvWriter().writeAll(listOf(listOf(participant.number.toString(),newTime.toString())), File("${generationDir.path}/control-point_${it.name}.csv"), append = true)
            }
            }
        }
}