package ru.emkn.kotlin.sms

class Collective(path: String) {
    val name: String
    val athleteList: List<Participant>
    init {
        collectiveParser(path).let {
            athleteList = it.second
            name = it.first
        }
    }
}