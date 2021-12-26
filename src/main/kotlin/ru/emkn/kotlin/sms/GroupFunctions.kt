package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import exceptions.UnexpectedValueException

fun Group.modifyGroup(
    configFileString: Map<String, String>,
    path: String
) {
    this.addDataWhenInitialise(
        configFileString["ВозрастОт"]?.toInt() ?: throw CSVFieldNamesException(path),
        configFileString["ВозрастДо"]?.toInt() ?: throw CSVFieldNamesException(path),
        chooseGender(configFileString["Пол"] ?: throw CSVFieldNamesException(path))
    )
}

fun getGroupByName(name: String, groups: List<Group>): Group {
    return groups.find { it.groupName == name } ?: throw  UnexpectedValueException("Нет группы с именем $name")
}

fun getGroupIndexByName(name: String, groups: List<Group>): Int {
    val group = getGroupByName(name, groups)
    return if (group != null) {
        groups.indexOf(group)
    } else throw UnexpectedValueException(name)
}