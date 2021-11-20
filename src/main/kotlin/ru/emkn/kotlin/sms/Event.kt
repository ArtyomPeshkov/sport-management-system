package ru.emkn.kotlin.sms

import java.time.*


class Event(path: String) {
    val name: String
    val date: LocalDate

    init {
        eventParser(path).let {
            name = it.first
            date = it.second
        }
    }

    override fun toString(): String {
        return "$name: $date"
    }
}