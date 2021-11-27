package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.IncorrectControlPointValue
import exceptions.ProblemWithCSVException
import log.debugC
import java.io.File

class Collective {
    val name:String
    private var athleteList: MutableList<Participant> = mutableListOf()
    var points = 0
    fun addParticipant(participant: Participant){
        athleteList.add(participant)
        points+=participant.points
    }

    fun makeParticipant(name:String, param: Map<String, String>, index: Int, path: String): Participant {
        parseLogger.debugC("Reading participant number ${index + 1} from $path")
        try {
            val participant= Participant(
                param["Группа"]?:throw CSVFieldNamesException(path),
                chooseSex(param["Группа"]!![0].toString()),
                param["Фамилия"]?:throw CSVFieldNamesException(path),
                param["Имя"]?:throw CSVFieldNamesException(path),
                param["Г.р."]?.toInt() ?:throw CSVFieldNamesException(path),
                param["Разр."] ?: throw CSVFieldNamesException(path)
            )
            participant.setCollective(name)
            return participant
        } catch (e: Exception) {
            throw CSVFieldNamesException(path)
        }
    }

    fun participantsParser(name:String,file: File):List<Participant> {

        if (file.readLines().size<3)
            throw ProblemWithCSVException(file.path)
        return             csvReader().readAllWithHeader(file.readLines().drop(1).joinToString("\n"))
                .mapIndexed() { index, it -> makeParticipant(name, it, index, file.path) }
    }

    fun collectiveParser(path: String): List<Participant>{
        parseLogger.debugC("Started reading participants from $path")
        return  participantsParser(name,readFile(path))
    }

    constructor(path: String)
    {
        val file = readFile(path)
        val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
        name = fileStrings[0].let {
            if (it.size != 5 || it.drop(1).any { el -> el.isNotEmpty() })
                throw CSVStringWithNameException(path)
            else it[0]
        }
        athleteList = collectiveParser(path).toMutableList()
    }
}