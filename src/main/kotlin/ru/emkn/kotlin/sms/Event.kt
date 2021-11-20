package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

object Event {
    val name: String
    val date: LocalDate

    init {
        competitionParser().let {
            name = it.first
            date = it.second
        }
    }
}

fun competitionParser(): Pair<String, LocalDate> {
    val path = readLine() ?: ""
    val file: File
    try {
        file = File(path)
    } catch (e: Exception) {
        throw Exception("Ошибка при чтении файла")
    }
    val rows = csvReader().readAllWithHeader(file)
    if (rows.size != 1)
        throw Exception("Неправильный формат соревнования")
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        return Pair(rows[0]["Название"] ?: "", LocalDate.parse(rows[0]["Дата"], formatter))
    } else
        throw Exception("Неправильные формат полей")
}