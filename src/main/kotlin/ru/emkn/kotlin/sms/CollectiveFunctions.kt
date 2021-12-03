package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import exceptions.ProblemWithCSVException
import log.universalC
import java.io.File

fun getCollectives(configurationFolder: List<File>, path: String) =
    collectivesParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
        ?: throw NotEnoughConfigurationFiles(path))

fun collectivesParser(applicationsFolder: File): List<Collective> {
    parseLogger.universalC(Colors.BLUE._name, "reading applications from folder ${applicationsFolder.path}", 'i')
    val applications =
        applicationsFolder.walk().toList().filter { it.extension == "csv" }
    return applications.map { getParticularCollective(readFile(it.path)) }
}

fun getParticularCollective(file: File):Collective {
    val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
    if (fileStrings.isEmpty())
        throw CSVStringWithNameException(file.path)
    val name = fileStrings[0].let {
        if (it.size != 5)
            throw CSVStringWithNameException(file.path)
        else it[0]
    }
    val collective = Collective(name)
    collective.addParticipants(participantsParser(name, file).toMutableList())
    return collective
}

fun participantsParser(name: String, file: File): List<Participant> {

    if (file.readLines().size < 3)
        throw ProblemWithCSVException(file.path)
    return csvReader().readAllWithHeader(file.readLines().drop(1).joinToString("\n"))
        .mapIndexed() { index, it -> makeParticipant(name, it, index, file.path) }
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