package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import exceptions.IncorrectControlPointValue

class Group(val groupName: String, val distance: Distance) {//группа
    val listParticipants: MutableList<Participant> = mutableListOf()
}
