package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.ProblemWithCSVException
import log.universalC
import java.io.File

fun participantsParser(name: String, file: File): List<Participant> {

    if (file.readLines().size < 3)
        throw ProblemWithCSVException(file.path)
    return csvReader().readAllWithHeader(file.readLines().drop(1).joinToString("\n"))
        .mapIndexed() { index, it -> makeParticipant(name, it, index, file.path) }
}

fun makeParticipant(name: String, param: Map<String, String>, index: Int, path: String): Participant {
    parseLogger.universalC(Colors.YELLOW._name, "Reading participant number ${index + 1} from $path")
    val participant = Participant(
        chooseSex(param["Пол"] ?: throw CSVFieldNamesException(path)),
        param["Фамилия"] ?: throw CSVFieldNamesException(path),
        param["Имя"] ?: throw CSVFieldNamesException(path),
        param["Г.р."]?.toInt() ?: throw CSVFieldNamesException(path),
        param["Разр."] ?: throw CSVFieldNamesException(path)
    )
    participant.setGroup(param["Группа"] ?: throw CSVFieldNamesException(path))
    //Переместить имя коллектива в конструктор и сделать makeParticipant более общим для 1 и 3 фаз
    participant.setCollective(name)
    return participant
}