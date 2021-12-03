package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.NotEnoughConfigurationFiles
import exceptions.UnexpectedValueException
import log.universalC
import java.io.File

fun getDistance(
    distanceName: String,
    configFileString: Map<String, String>,
    controlPoints: MutableSet<ControlPoint>
): Distance {
    val pointsList: MutableList<ControlPoint> = mutableListOf()
    configFileString.values.drop(1).forEach { pointName ->
        if (pointName != "") {
            val requiredPoint = ControlPoint(pointName)
            val point = controlPoints.find { it == requiredPoint }
            if (point != null)
                pointsList.add(point)
            else {
                pointsList.add(requiredPoint)
                controlPoints.add(requiredPoint)
            }
        }
    }
    if (pointsList.isEmpty())
        throw UnexpectedValueException("В дистанции $distanceName нет контрольных точек!")
    val distance = Distance(distanceName)
    distance.addAllPoints(pointsList)
    return distance
}

fun distancesParser(distances: File, controlPoints: MutableSet<ControlPoint>): Map<String, Distance> {
    parseLogger.universalC(Colors.BLUE._name, "reading distances from file ${distances.path}", 'i')
    val distanceStrings = csvReader().readAllWithHeader(distances)
    return distanceStrings.associate {
        val distanceName = it["Название"] ?: throw CSVFieldNamesException(distances.path)
        Pair(
            distanceName,
            getDistance(distanceName, it, controlPoints)
        )
    }
}

fun getDistances(
    configurationFolder: List<File>,
    path: String,
    controlPoints: MutableSet<ControlPoint> = mutableSetOf()
) =
    distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
        ?: throw NotEnoughConfigurationFiles(path), controlPoints)