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
        throw ProblemWithFilePathException(path)
    }
}

fun eventParser(path: String): Pair<String, LocalDate> {
    parseLogger.debugC("Parsing event file: $path")
    val file = readFile(path)
    val rows = csvReader().readAllWithHeader(file)
    if (rows.size != 1)
        throw ProblemWithCSVException(path)
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        return Pair(rows[0]["Название"] ?: "", LocalDate.parse(rows[0]["Дата"], formatter))
    } else
        throw CSVStringWithNameException(path)
}

fun chooseSex(sex: String): Sex {
    return when (sex) {
        "М", "M", "m", "м" -> Sex.MALE
        "Ж", "F", "ж", "f" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}


fun makeParticipant(param: Map<String, String>, index: Int, path: String): Participant {
    parseLogger.debugC("Reading participant number ${index + 1} from $path")
    try {
        return Participant(
            param["Группа"]!!,
            chooseSex(param["Группа"]!![0].toString()),
            param["Фамилия"]!!,
            param["Имя"]!!,
            param["Г.р."]!!.toInt(),
            param["Разр."]!!
        )
    } catch (e: Exception) {
        throw CSVFieldNamesException(path)
    }
}

fun participantsParser(name:String,file: File):Pair<String, List<Participant>> {

    if (file.readLines().size<3)
        throw ProblemWithCSVException(file.path)
    return Pair(
        name,
        csvReader().readAllWithHeader(file.readLines().drop(1).joinToString("\n"))
            .mapIndexed() { index, it -> makeParticipant(it, index, file.path) })
}

fun collectiveParser(path: String): Pair<String, List<Participant>>{
    parseLogger.debugC("Parsing collective file: $path")

    val file = readFile(path)
    val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
    if (file.readLines().size<3)
        throw ProblemWithCSVException(path)
    val name = fileStrings[0].let {
        if (it.size != 6 || it.drop(1).any { el -> el.isNotEmpty() })
            throw CSVStringWithNameException(path)
        else it[0]
    }
    parseLogger.debugC("Started reading participants from $path")
    if (csvReader().readAll(file.readLines().drop(1).first())[0] != listOf(
            "Группа",
            "Фамилия",
            "Имя",
            "Г.р.",
            "Разр."
        )
    ) throw CSVFieldNamesException(path)
    // Не знаю, стоит ли так делать, но я передаю файл в makeParticipant, чтобы указать файл в котором получена ошибка
    return  participantsParser(name,file)
}

fun readGroupsFromFile(path: String) {
    val file = readFile(path)
    val groupList = Groups
    file.forEachLine {
        if (it != "Название,Дистанция") {
            try {
                val nameAndDistance = it.split(",")
                if (nameAndDistance.size != 2) {
                    throw TODO() //в строке либо недостает данных, либо есть что-то лишнее
                }
                else if (nameAndDistance[1].toIntOrNull() == null ) {
                    throw TODO() //дистанция не интовая
                }
                else {
                    val group = Group(nameAndDistance[0])
                    //TODO(group.distance = distances.getDistanceByName(nameAndDistance[1]))
                    groupList.groupList.add(group)
                }
            } catch (e:Exception) {
                TODO()
            }
        }
    }
}