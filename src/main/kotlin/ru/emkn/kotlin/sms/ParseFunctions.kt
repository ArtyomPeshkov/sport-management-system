package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CollectiveFileStringException
import exceptions.ProblemWithCSV
import exceptions.ProblemWithFilePath
import exceptions.SexException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.LocalDate

val parseLogger: Logger = LoggerFactory.getLogger("Parse")

fun readFile(path: String): File {
    parseLogger.debug("Reading file: $path")
    try {
        return File(path)
    } catch (e: Exception) {
        throw ProblemWithFilePath(path)
    }
}

fun eventParser(path: String): Pair<String, LocalDate> {
    parseLogger.debug("Parsing event file: $path")
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
    return when (sex) {
        "М", "M", "m", "м" -> Sex.MALE
        "Ж", "F", "ж", "f" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}

fun makeParticipant(param: Map<String, String>, index: Int, path: String): Participant {
    parseLogger.debug("Reading participant number ${index + 1} from $path")
    try {
        return Participant(
            param["Фамилия"]!!,
            param["Имя"]!!,
            chooseSex(param["Пол"]!!),
            param["Год рождения"]!!.toInt(),
            param["Разряд"]!!,
            param["Группа"]!!
        )
    } catch (e: Exception) {
        throw CollectiveFileStringException(path, index)
    }
}

fun collectiveParser(path: String): Pair<String, List<Participant>> {
    parseLogger.debug("Parsing collective file: $path")
    val file = readFile(path)
    val name = csvReader().readAll(file.readText().substringBefore("\n"))[0].let {
        if (it.size != 6 || it.drop(1).any { el -> el.isNotEmpty() })
            throw CollectiveFileStringException(path)
        else it[0]
    }
    parseLogger.debug("Started reading participants from $path")
    val lines = file.readLines()
    if (csvReader().readAll(lines.drop(1).first())[0] != listOf(
            "Фамилия",
            "Имя",
            "Пол",
            "Год рождения",
            "Разряд",
            "Группа"
        )
    ) throw TODO() // Надо класс.
    // Не знаю, стоит ли так делать, но я передаю файл в makeParticipant, чтобы указать файл в котором получена ошибка
    return Pair(
        name,
        csvReader().readAllWithHeader(lines.drop(1).joinToString("\n"))
            .mapIndexed() { index, it -> makeParticipant(it, index, path) })
}