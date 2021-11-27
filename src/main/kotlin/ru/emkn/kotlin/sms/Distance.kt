package ru.emkn.kotlin.sms

import exceptions.CSVFieldNamesException
import exceptions.IncorrectControlPointValue

class Distance(val name: String) {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()

    constructor(configFileString: Map<String, String>, path: String) : this(
        configFileString["Название"] ?: throw CSVFieldNamesException(path)
    ) {
        configFileString.values.drop(1).forEach {
            pointsList.add(
                ControlPoint(
                    it.toIntOrNull() ?: if (it == "") return@forEach else throw IncorrectControlPointValue(it)
                )
            )
        }
    }
}

data class ControlPoint(val number: Int, val isFinal: Boolean = false)