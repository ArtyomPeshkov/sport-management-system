package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.debugC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate

val parseLogger: Logger = LoggerFactory.getLogger("Parse")

fun readFile(path: String): File {
    parseLogger.debugC("Reading file: $path")
    try {
        return File(path)
    } catch (e: Exception) {
        throw ProblemWithFilePath(path)
    }
}

fun eventParser(path: String): Pair<String, LocalDate> {
    parseLogger.debugC("Parsing event file: $path")
    val file = readFile(path)
    val rows = csvReader().readAllWithHeader(file)
    if (rows.size != 1)
        throw ProblemWithCSV(path)
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        return Pair(rows[0]["Название"] ?: "", LocalDate.parse(rows[0]["Дата"], formatter))
    } else
        throw ProblemWithCSV(path)
}

fun chooseSex(sex: String): Sex {
    return when(sex) {
        "М", "M", "m", "м" -> Sex.MALE
        "Ж", "F", "ж", "f" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}

fun makeParticipant(param: List<String>, index: Int,path: String): Participant {
    parseLogger.debugC("Reading participant number ${index+1} from $path")
    try {
        return Participant(param[0], param[1], chooseSex(param[2]), param[3].toInt(), param[4], param[5])
    } catch (e: Exception) {
        throw CollectiveFileStringException(path,index)
    }
}

fun collectiveParser(path: String): Pair<String, List<Participant>>{
    parseLogger.debugC("Parsing collective file: $path")
    val file = readFile(path)
    val name = csvReader().readAll(file.readText().substringBefore('\n')).let {
        if (it.isEmpty()) ""
        else if (it[0].size != 6 || it[0].drop(1).any { el -> el.isNotEmpty() }
        ) throw CollectiveFileStringException(path)
        else it[0][0]
    }
    parseLogger.debugC("Started reading participants from $path")
    val lines = file.readLines()
    // Не знаю, стоит ли так делать, но я передаю файл в makeParticipant, чтобы указать файл в котором получена ошибка
    return Pair(name, csvReader().readAll(lines.subList(1, lines.size).joinToString("\n")).mapIndexed() { index, it -> makeParticipant(it, index,path) })
}