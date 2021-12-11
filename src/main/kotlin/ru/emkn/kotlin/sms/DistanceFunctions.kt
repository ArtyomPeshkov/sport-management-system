package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException

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
