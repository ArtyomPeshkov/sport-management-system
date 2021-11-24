package ru.emkn.kotlin.sms

import exceptions.IllegalTimeFormatException
import exceptions.UnexpectedValueException
import javax.print.attribute.standard.MediaSize
import kotlin.math.sign

class Time {
    var hours: Int
    var minutes: Int
    var seconds: Int

    private fun checkDateFormat(hours: Int, minutes: Int, seconds: Int): Boolean {
        if (hours !in 0..23 || minutes !in 0..59 || seconds !in 0..59)
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

    private val timeInSeconds: Int
        get() = hours * 360 + minutes * 60 + seconds

    operator fun compareTo(other: Time): Int = (timeInSeconds - other.timeInSeconds).sign

    override fun toString(): String = "$hours:$minutes:$seconds"

}