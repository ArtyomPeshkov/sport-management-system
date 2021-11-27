package ru.emkn.kotlin.sms

import exceptions.ProblemWithFilePathException
import exceptions.SexException
import log.debugC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val parseLogger: Logger = LoggerFactory.getLogger("Parse")

fun readFile(path: String): File {
    parseLogger.debugC("Reading file: $path")
    try {
        return File(path)
    } catch (e: Exception) {
        throw ProblemWithFilePathException(path)
    }
}

fun chooseSex(sex: String): Sex {
    return when (sex) {
        "М", "M", "m", "м" -> Sex.MALE
        "Ж", "F", "ж", "f" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}

fun main(args: Array<String>) {
    val event = Event("csvFiles/configuration")
    println(event.toString())
}
