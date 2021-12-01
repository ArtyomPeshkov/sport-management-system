package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import exceptions.IncorrectControlPointValue

class Distance(val name: String) {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()

    fun getPointsList(): List<ControlPoint>{
        return pointsList.toMutableList()
    }
    constructor(configFileString: Map<String, String>, path: String, controlPoints: MutableSet<ControlPoint>) : this(
        configFileString["Название"] ?: throw CSVFieldNamesException(path)
    ) {
        configFileString.values.drop(1).forEach { name ->
            val requiredPoint = ControlPoint(name)
            val point = controlPoints.find { it==requiredPoint}
            if (point!=null)
                pointsList.add(point)
            else if (name != "") {
                pointsList.add(requiredPoint)
                controlPoints.add(requiredPoint)
            }
            else
                return@forEach
        }
    }

    override fun toString(): String {
        val s = StringBuilder("$name, Контрольные точки: $pointsList")
        return s.toString()
    }
}

data class ControlPoint(val name: String)

data class ControlPointWithTime(val name: String, val time: Time)
{
    fun toCSV():List<String>{
        return listOf(name,time.toString())
    }
}

