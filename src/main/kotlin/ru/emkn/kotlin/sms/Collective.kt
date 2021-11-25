package ru.emkn.kotlin.sms

class Collective(val name: String) {
    val athleteList: MutableList<Participant> = mutableListOf()
    var points = 0
    fun addParticipant(participant: Participant){
        athleteList.add(participant)
        points+=participant.points
    }
}