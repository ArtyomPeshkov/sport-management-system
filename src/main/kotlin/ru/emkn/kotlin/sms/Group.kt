package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck
import log.universalC

class Group(name: String, dist: Distance) {
    //группа
    val groupName: String
    val distance: Distance
    var ageFrom: Int = 0
        private set
    var ageTo: Int = 0
        private set
    lateinit var sex: Sex
        private set

    init {
        emptyNameCheck(name,"Имя группы пустое")
        groupName=name
        if (dist.getPointsList().isEmpty())
            throw UnexpectedValueException("У дистанции  $dist нет контрольных точек")
        distance=dist
    }

    val listParticipants: MutableList<Participant> = mutableListOf()
    fun addParticipant(participant: Participant) {
        listParticipants.add(participant)
    }
    fun addParticipants(participants: Collection<Participant>) {
        listParticipants.addAll(participants)
    }

    fun addDataWhenInitialise(ageFrom: Int,ageTo:Int,sex: Sex)
    {
        if (ageTo<0 || ageFrom<0 || ageFrom>ageTo)
            throw UnexpectedValueException("Проблема с возрастными ограничениями группы: Минимальный возраст = $ageFrom; Максимальный возраст = $ageTo")
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