package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.ProblemWithCSVException
import log.universalC
import java.io.File

/** считывает участников из csv-файла */
fun participantsParser(team: String, file: File): List<Participant> {
    if (file.readLines().size < 2)
        throw ProblemWithCSVException(file.path)
    return csvReader().readAllWithHeader(file.readLines().drop(1).joinToString("\n"))
        .mapIndexed() { index, it -> makeParticipant(it, index, file.path,team) }
}

fun makeParticipant(param: Map<String, String>, index: Int, path: String,team: String="",group:String=""): Participant {
    parseLogger.universalC(Colors.YELLOW._name, "Reading participant number ${index + 1} from $path")
    val participant = Participant(
        chooseGender(param["Пол"] ?: throw CSVFieldNamesException(path)),
        param["Фамилия"] ?: throw CSVFieldNamesException(path),
        param["Имя"] ?: throw CSVFieldNamesException(path),
        param["Г.р."]?.toInt() ?: throw CSVFieldNamesException(path),
        param["Разр."] ?: throw CSVFieldNamesException(path)
    )
    participant.setGroup(param["Группа"] ?: group)
    participant.setTeam(param["Коллектив"] ?: team)
    return participant
}