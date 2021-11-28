package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException

class Group {
    //группа
    val groupName: String
    val distance: Distance
    var ageFrom: Int = 0
    var ageTo: Int = 0
    var sex: Sex = Sex.NB
    val listParticipants: MutableList<Participant> = mutableListOf()
    fun addParticipant(participant: Participant) {
        listParticipants.add(participant)
    }

    constructor(name: String, dist: Distance) {
        groupName = name
        distance = dist
    }

    constructor(
        configFileString: Map<String, String>,
        distance: Distance,
        path: String
    ) : this(configFileString["Название"] ?: throw CSVFieldNamesException(path), distance) {
        sex = chooseSex(configFileString["Пол"] ?: throw CSVFieldNamesException(path))
        ageFrom = configFileString["ВозрастОт"]?.toInt() ?: throw CSVFieldNamesException(path)
        ageTo = configFileString["ВозрастДо"]?.toInt() ?: throw CSVFieldNamesException(path)
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
