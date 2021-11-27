package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException

class Group {
    //группа
    val groupName: String
    val distance: Distance
    val ageFrom: Int
    val ageTo: Int
    val sex: Sex
    val listParticipants: MutableList<Participant> = mutableListOf()
    fun addParticipant(participant: Participant) {
        listParticipants.add(participant)
    }

    constructor(configFileString: Map<String, String>, distance: Distance, path: String) {
        groupName = configFileString["Название"] ?: throw CSVFieldNamesException(path)
        this.distance = distance
        sex = chooseSex(configFileString["Пол"] ?: throw CSVFieldNamesException(path))
        ageFrom = configFileString["ВозрастОт"]?.toInt() ?: throw CSVFieldNamesException(path)
        ageTo = configFileString["ВозрастДо"]?.toInt() ?: throw CSVFieldNamesException(path)
    }
}
