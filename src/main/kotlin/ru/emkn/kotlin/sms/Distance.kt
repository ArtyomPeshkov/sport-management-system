package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import exceptions.IncorrectControlPointValue
import exceptions.UnexpectedValueException

class Distance(val name: String) {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()

    fun getPointsList(): List<ControlPoint> {
        return pointsList.toMutableList()
    }

    fun addPoint(point: ControlPoint) {
        pointsList.add(point)
    }

    fun addAllPoints(points: Collection<ControlPoint>) {
        pointsList.addAll(points)
    }

    override fun toString(): String {
        val s = StringBuilder("$name, Контрольные точки: $pointsList")
        return s.toString()
    }
}

data class ControlPoint(val name: String)

data class ControlPointWithTime(val name: String, val time: Time)

