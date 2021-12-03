package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.ProblemWithCSVException
import log.universalC
import java.io.File

class Collective(val name: String) {
    var athleteList: MutableList<Participant> = mutableListOf()
    var points = 0

    fun addParticipant(participant: Participant) {
        athleteList.add(participant)
        points += participant.points
    }

    fun addParticipants(participants: Collection<Participant>) {
        athleteList.addAll(participants)
    }
}