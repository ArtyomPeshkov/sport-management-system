package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.universalC
import java.io.File

class Team(name: String) {
    val name: String
    var athleteList: MutableList<Participant> = mutableListOf()
    var points = 0

    init {
        emptyNameCheck(name, "Пустое имя коллектива")
        this.name = name
    }

    fun addParticipant(participant: Participant) {
        athleteList.add(participant)
        if (participant.points < 0)
            throw UnexpectedValueException("Количество очков у участника ${participant.number} отрицательно")
        points += participant.points
    }

    fun addParticipants(participants: Collection<Participant>) {
        athleteList.addAll(participants)
    }
}