package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck

class Distance(name: String) {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()
    val name: String

    init {
        emptyNameCheck(name, "Пустое имя дистанции")
        this.name = name
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
        if (points.any { it.name.isBlank() })
            throw UnexpectedValueException("Пустое имя контрольной точки")
        pointsList.addAll(points)
    }

    fun checkProtocolPointsCorrectness(
        participant: Participant,
        participantDistance: Map<Int, List<ControlPointWithTime>>
    ): String {
        val participantControlPoints = participantDistance[participant.number]
        if (!(participantControlPoints?.map { it.point }?: listOf(
                ControlPoint(""))).containsAll(pointsList))
            return "Снят"
        val first = ControlPointWithTime(ControlPoint("Start"), participant.startTime)
        val lastControlPointTime =
            participantControlPoints?.find { it.point == pointsList.last() }?.time ?: return "Снят"
        participantControlPoints.forEach {
            val previousControlPointIndex = pointsList.indexOf(it.point)
            val previousControlPointWithTime = if (previousControlPointIndex != 0) {
                val previousControlPoint = pointsList[previousControlPointIndex - 1]
                participantControlPoints.find { previousControlPoint == it.point }
                    ?: throw UnexpectedValueException(previousControlPoint.name)
            } else
                first
            if (it.time <= previousControlPointWithTime.time)
                return "Снят"
        }
        return (lastControlPointTime - first.time).toString()
    }

    override fun toString(): String {
        val s = StringBuilder("$name, Контрольные точки: $pointsList")
        return s.toString()
    }
}

