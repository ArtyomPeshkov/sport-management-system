package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException


data class DistanceTypeData(val type: DistanceType, var numberOfPoints:Int?,val orderIsEssential: Boolean)

/** присуждает дистанции тип, в зависимости от того, что указано в файле (может быть указано в двух видах) */
fun chooseType(type:String) = when (type){
        "all","ALL_POINTS" -> DistanceType.ALL_POINTS
        "some","SOME_POINTS" -> DistanceType.SOME_POINTS
        else -> throw UnexpectedValueException("Неожиданный тип: $type")
    }

fun checkType(type: String, numberOfPoints:String,orderIsEssential: String):DistanceTypeData{
    val dType=chooseType(type)
    var number:Int? = null
    if (dType != DistanceType.ALL_POINTS)
        number = numberOfPoints.toIntOrNull()
            ?: throw UnexpectedValueException("неправильное количество необходимых точек: $numberOfPoints")
    return DistanceTypeData(dType, number, true)
    /*return if (dType==DistanceType.ALL_POINTS)
            DistanceTypeData(dType,number,true)
        else
            DistanceTypeData(dType,number,false)*/
}

/** возвращает дистанцию с необходимыми значениями (работает с отформатированнной строкой полученной ранее из файла) */
fun getDistance(
    distanceName: String,
    configFileString: Map<String, String>,
    controlPoints: MutableList<ControlPoint>
): Distance {
    val pointsList: MutableList<ControlPoint> = mutableListOf()
    val type = configFileString["Тип"] ?: throw UnexpectedValueException("Проблемы с типом дистанции $distanceName")
    val numberOfPoints = configFileString["Количество точек"]
        ?: throw UnexpectedValueException("Проблемы с количеством необходимых точек у дистанции $distanceName")
    val orderIsEssential = configFileString["Порядок"]
        ?: throw UnexpectedValueException("Проблемы с требованием к порядку прохождения точек у дистанции $distanceName")

    configFileString.values.drop(4).forEach { pointName ->
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
    val distType = checkType(type, numberOfPoints, orderIsEssential)
    val distance = Distance(distanceName, distType)
    if (distType.type == DistanceType.ALL_POINTS)
        distType.numberOfPoints = pointsList.size
    distance.addAllPoints(pointsList)
    return distance
}
