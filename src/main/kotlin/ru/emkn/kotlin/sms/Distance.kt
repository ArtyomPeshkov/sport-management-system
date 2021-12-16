package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck

class Distance(name: String, type: DistanceTypeData) {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()
    val name: String
    val type: DistanceTypeData

    init {
        emptyNameCheck(name, "Пустое имя дистанции")
        this.name = name
        this.type = type
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
        val start = ControlPointWithTime(ControlPoint("Start"), participant.startTime)
        var participantControlPoints = participantDistance[participant.number] ?: return "Снят"
        participantControlPoints = participantControlPoints.sortedBy { it.time.timeInSeconds }
        return when (type.type) {
            DistanceType.ALL_POINTS -> {
                if (participantControlPoints.map { it.point } != pointsList)
                    return "Снят"
                (participantControlPoints.maxByOrNull { it.time.timeInSeconds }!!.time - start.time).toString()
            }
            DistanceType.SOME_POINTS ->{
                var currentNumberOfPassedPoints = 0
                participantControlPoints.forEach {
                    var currentIndex = 0
                    while (currentIndex != pointsList.size && it.point!=pointsList[currentIndex])
                        currentIndex++
                    if (currentIndex==pointsList.size)
                        return@forEach
                    if (pointsList[currentIndex]==it.point)
                        currentNumberOfPassedPoints++
                    if (currentNumberOfPassedPoints == type.numberOfPoints)
                        return (it.time - start.time).toString()
                }
                return "Снят"
            }
        }
    }

    override fun toString(): String {
        val s = StringBuilder("$name, Контрольные точки: $pointsList")
        return s.toString()
    }
}

