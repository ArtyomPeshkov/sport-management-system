package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck

class Distance(name: String) {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()
    val name:String
    init {
        emptyNameCheck(name,"Пустое имя дистанции")
        this.name=name
    }

    fun getPointsList(): List<ControlPoint> {
        return pointsList.toMutableList()
    }

    fun addPoint(point: ControlPoint) {
        if (point.name.isBlank())
            throw UnexpectedValueException("Пустое имя контрольной точки")
        pointsList.add(point)
    }

    fun addAllPoints(points: Collection<ControlPoint>) {
        if (points.any{it.name.isBlank()})
            throw UnexpectedValueException("Пустое имя контрольной точки")
        pointsList.addAll(points)
    }

    override fun toString(): String {
        val s = StringBuilder("$name, Контрольные точки: $pointsList")
        return s.toString()
    }
}

