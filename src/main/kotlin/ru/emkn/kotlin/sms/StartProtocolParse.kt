package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import log.universalC
import java.io.File

fun parseStartProtocolFiles(startsFolder: File, groups: List<Group>) {
    parseLogger.universalC(Colors.BLUE._name, "reading start protocols from folder ${startsFolder.path}", 'i')
    val startInfo =
        startsFolder.walk().toList().filter { it.extension == "csv" }
    startInfo.map { parseStartProtocol(it, groups) }
}

fun getStartProtocolFolder(configurationFolder: List<File>, path: String, groups: List<Group>) =
    parseStartProtocolFiles(configurationFolder.find { it.name.substringAfterLast('/') == "starts" }
        ?: throw NotEnoughConfigurationFiles(path), groups)

fun parseStartProtocol(protocol: File, groups: List<Group>) {
    val fileStrings = csvReader().readAll(protocol.readText().substringBefore("\n"))
    val nameOfGroup = fileStrings[0].let {
        if (it[0] == "")
            throw CSVStringWithNameException(protocol.path)
        else it[0]
    }
    val indexOfGroup = getGroupIndexByName(nameOfGroup, groups)
    val participantData = csvReader().readAllWithHeader(protocol.readLines().drop(1).joinToString("\n"))
    participantData.forEach {
        val participant = Participant(
            nameOfGroup,
            chooseSex(nameOfGroup[0].toString()),
            it["Фамилия"] ?: throw CSVFieldNamesException(protocol.path),
            it["Имя"] ?: throw CSVFieldNamesException(protocol.path),
            it["Г.р."]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
            it["Разр."] ?: throw CSVFieldNamesException(protocol.path)
        )
        participant.setCollective(it["Коллектив"] ?: throw CSVFieldNamesException(protocol.path))
        participant.setStart(
            it["Номер"]?.toInt() ?: throw CSVFieldNamesException(protocol.path),
            Time(it["Стартовое время"] ?: throw CSVFieldNamesException(protocol.path))
        )
        groups[indexOfGroup].addParticipant(participant)

    }
}
