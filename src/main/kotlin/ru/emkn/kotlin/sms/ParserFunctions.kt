package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File
import java.time.LocalDate

fun readFile(path: String): File {
    try {
        return File(path)
    } catch (e: Exception) {
        throw Exception("Ошибка при чтении файла")
    }
}

fun eventParser(path: String): Pair<String, LocalDate> {
    val file = readFile(path)
    val rows = csvReader().readAllWithHeader(file)
    if (rows.size != 1)
        throw Exception("Неправильный формат соревнования")
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        return Pair(rows[0]["Название"] ?: "", LocalDate.parse(rows[0]["Дата"], formatter))
    } else
        throw Exception("Неправильные формат полей")
}

fun collectiveParser(path: String): List<Person> {
    val res = mutableListOf<Person>()
    val file = readFile(path)
    val rows = csvReader().readAllWithHeader(file)
    return res
}