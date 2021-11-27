package ru.emkn.kotlin.sms

import exceptions.IllegalTimeFormatException
import exceptions.NegativeSubstractTime
import exceptions.UnexpectedValueException
import kotlin.math.sign

class Time {
    private var hours: Int
    private var minutes: Int
    private var seconds: Int

    private fun checkDateFormat(hours: Int, minutes: Int, seconds: Int): Boolean {
        if (hours in 0..23 || minutes in 0..59 || seconds in 0..59)
            return false
        return true
    }

    constructor(time: String) {
        val timeParametersHhMmSs = List(3) { time.split(':')[it].toIntOrNull() }
        if (timeParametersHhMmSs.size != 3 || timeParametersHhMmSs.any { it == null } || checkDateFormat(
                timeParametersHhMmSs[0] ?: -1,
                timeParametersHhMmSs[1] ?: -1,
                timeParametersHhMmSs[2] ?: -1
            ))
            throw IllegalTimeFormatException(time)
        hours = timeParametersHhMmSs[0] ?: throw UnexpectedValueException(timeParametersHhMmSs[0])
        minutes = timeParametersHhMmSs[1] ?: throw UnexpectedValueException(timeParametersHhMmSs[1])
        seconds = timeParametersHhMmSs[2] ?: throw UnexpectedValueException(timeParametersHhMmSs[2])
    }

    constructor(hours: Int, minutes: Int, seconds: Int) {
        if (checkDateFormat(hours, minutes, seconds))
            throw IllegalTimeFormatException("$hours:$minutes:$seconds")
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }

    constructor(sec: Int) {
        if (sec < 0)
            throw UnexpectedValueException(sec)
        val hours = sec / 3600
        val minutes = sec % 3600 / 60
        val seconds = sec % 60
        if (checkDateFormat(hours, minutes, seconds))
            throw IllegalTimeFormatException(
                "$hours" + ":" + "$minutes".padStart(2, '0') + ":" + "$seconds".padStart(
                    3,
                    '0'
                )
            )
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }

    operator fun plus(other: Time): Time {
        return Time(this.timeInSeconds + other.timeInSeconds)
    }

    operator fun minus(other: Time): Time {
        if (this < other) throw NegativeSubstractTime(this, other)
        return Time(this.timeInSeconds - other.timeInSeconds)
    }

    val timeInSeconds: Int
        get() = hours * 3600 + minutes * 60 + seconds

    operator fun compareTo(other: Time): Int = (timeInSeconds - other.timeInSeconds).sign

    override fun toString(): String = "$hours" + ":" + "$minutes".padStart(2, '0') + ":" + "$seconds".padStart(2, '0')

}