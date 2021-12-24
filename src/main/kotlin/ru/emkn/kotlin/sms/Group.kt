package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck

class Group(name: String, dist: Distance) {
    val groupName: String
    val distance: Distance
    var ageFrom: Int = 0
        private set
    var ageTo: Int = 0
        private set
    lateinit var gender: Gender
        private set

    init {
        emptyNameCheck(name, "Имя группы пустое")
        groupName = name
        if (dist.getPointsList().isEmpty())
            throw UnexpectedValueException("У дистанции  $dist нет контрольных точек")
        distance = dist
    }

    val listParticipants: MutableList<Participant> = mutableListOf()

    /** добавляет переданного в функцию участника в список участников для данной группы */
    fun addParticipant(participant: Participant) {
        listParticipants.add(participant)
    }

    /** добавляет всех участников из переданного в функцию списка в список участников для данной группы */
    fun addParticipants(participants: Collection<Participant>) {
        listParticipants.addAll(participants)
    }

    /** выставляет возрастные и гендерные ограничения для данной группы */
    fun addDataWhenInitialise(ageFrom: Int, ageTo: Int, gender: Gender) {
        if (ageTo < 0 || ageFrom < 0 || ageFrom > ageTo)
            throw UnexpectedValueException("Проблема с возрастными ограничениями группы: Минимальный возраст = $ageFrom; Максимальный возраст = $ageTo")
        this.ageFrom = ageFrom
        this.ageTo = ageTo
        this.gender = gender
    }

    fun toStringFull(): String {
        val s = StringBuilder(this.toString())
        s.append("Пол: $gender; Минимальный возраст: $ageFrom; Максимальный возраст: $ageTo")
        return s.toString()
    }

    override fun toString(): String {
        val s = StringBuilder("Название: $groupName\nДистанция: $distance\nСписок участников: $listParticipants\n")
        return s.toString()
    }
}