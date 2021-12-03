package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import log.universalC

class Group(name: String, dist: Distance) {
    //группа
    val groupName: String = name
    val distance: Distance = dist
    var ageFrom: Int = 0
        private set
    var ageTo: Int = 0
        private set
    lateinit var sex: Sex
        private set

    val listParticipants: MutableList<Participant> = mutableListOf()
    fun addParticipant(participant: Participant) {
        listParticipants.add(participant)
    }
    fun addParticipants(participants: Collection<Participant>) {
        listParticipants.addAll(participants)
    }

    fun addDataWhenInitialise(ageFrom: Int,ageTo:Int,sex: Sex)
    {
        this.ageFrom=ageFrom
        this.ageTo=ageTo
        this.sex=sex
    }

    fun toStringFull():String{
        val s = StringBuilder(this.toString())
        s.append("Пол: $sex; Минимальный возраст: $ageFrom; Максимальный возраст: $ageTo")
        return s.toString()
    }

    override fun toString(): String {
        val s = StringBuilder("Название: $groupName\nДистанция: $distance\nСписок участников: $listParticipants\n")
        return s.toString()
    }
}