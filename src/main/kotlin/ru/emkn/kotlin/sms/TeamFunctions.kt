package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.universalC
import java.io.File

fun getTeams(configurationFolder: List<File>, path: String) =
    teamsParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
        ?: throw NotEnoughConfigurationFiles(path))

fun teamsParser(applicationsFolder: File): List<Team> {
    parseLogger.universalC(Colors.BLUE._name, "reading applications from folder ${applicationsFolder.path}", 'i')
    val applications =
        applicationsFolder.walk().toList().filter { it.extension == "csv" }
    return applications.map { getParticularTeam(readFile(it.path)) }
}

fun getParticularTeam(file: File): Team {
    val fileStrings = csvReader().readAll(file.readText().substringBefore("\n"))
    if (fileStrings.isEmpty())
        throw CSVStringWithNameException(file.path)
    val name = fileStrings[0].let {
        if (it.size != 5)
            throw CSVStringWithNameException(file.path)
        else it[0]
    }
    val team = Team(name)
    team.addParticipants(participantsParser(name, file))
    return team
}