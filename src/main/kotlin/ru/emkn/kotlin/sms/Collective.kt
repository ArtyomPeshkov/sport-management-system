package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.ProblemWithCSVException
import log.universalC
import java.io.File

class Collective {
    val name: String
    var athleteList: MutableList<Participant> = mutableListOf()
    var points = 0

    fun addParticipant(participant: Participant) {
        athleteList.add(participant)
        points += participant.points
    }

    fun makeParticipant(name: String, param: Map<String, String>, index: Int, path: String): Participant {
        parseLogger.universalC(Colors.YELLOW._name, "Reading participant number ${index + 1} from $path")
        try {
            val participant = Participant(
                param["Группа"] ?: throw CSVFieldNamesException(path),
                chooseSex(param["Пол"] ?: throw CSVFieldNamesException(path)),
                param["Фамилия"] ?: throw CSVFieldNamesException(path),
                param["Имя"] ?: throw CSVFieldNamesException(path),
                param["Г.р."]?.toInt() ?: throw CSVFieldNamesException(path),
                param["Разр."] ?: throw CSVFieldNamesException(path)
            )
            participant.setCollective(name)
            return participant
        } catch (e: Exception) {
            throw CSVFieldNamesException(path)
        }
    }

    fun participantsParser(name: String, file: File): List<Participant> {

        if (file.readLines().size < 3)
            throw ProblemWithCSVException(file.path)
        return csvReader().readAllWithHeader(file.readLines().drop(1).joinToString("\n"))
            .mapIndexed() { index, it -> makeParticipant(name, it, index, file.path) }
    }

    fun collectiveParser(path: String): List<Participant> {
        parseLogger.universalC(Colors.YELLOW._name, "Started reading participants from $path")
        return participantsParser(name, readFile(path))
    }
   //  TODO("передавать что-то другое (не строку)")
    constructor(file: File) {
        val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
        if (fileStrings.isEmpty())
            throw CSVStringWithNameException(path)
        name = fileStrings[0].let {
            if (it.size != 5)
                throw CSVStringWithNameException(file.path)
            else it[0]
        }
        athleteList = collectiveParser(file.path).toMutableList()
    }

    constructor(name: String) {
        this.name = name
    }
}