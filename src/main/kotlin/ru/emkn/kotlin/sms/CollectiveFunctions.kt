package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
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

fun getParticularCollective(file: File): Collective {
    val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
    if (fileStrings.isEmpty())
        throw CSVStringWithNameException(file.path)
    val name = fileStrings[0].let {
        if (it.size != 5)
            throw CSVStringWithNameException(file.path)
        else it[0]
    }
    val collective = Collective(name)
    collective.addParticipants(participantsParser(name, file))
    return collective
}