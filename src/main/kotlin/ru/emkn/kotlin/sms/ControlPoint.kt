package ru.emkn.kotlin.sms

data class ControlPoint(val name: String)

data class ControlPointWithTime(val point: ControlPoint, val time: Time)