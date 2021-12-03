package ru.emkn.kotlin.sms

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

