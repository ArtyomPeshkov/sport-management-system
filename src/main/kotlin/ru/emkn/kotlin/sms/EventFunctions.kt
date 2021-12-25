package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import exceptions.ProblemWithCSVException
import log.universalC
import java.io.File
import java.time.LocalDate

data class NameDate(val name: String, val date: LocalDate)

/** позволяет считать из csv-файла название и дату соревнования */
fun getNameAndDate(configurationFolder: List<File>, path: String): NameDate {
    val rows = csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
        ?: throw NotEnoughConfigurationFiles(path))
    return if (rows.size != 1)
        throw ProblemWithCSVException(path)
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        NameDate(
            rows[0]["Название"] ?: throw CSVStringWithNameException(path),
            LocalDate.parse(rows[0]["Дата"], formatter)
        )
    } else
        throw CSVStringWithNameException(path)
}

/** функция, используя полученные ранее данные, формирует стартовые протоколы */
fun Event.makeStartProtocols(configurationFolder: String) {
    val startDir = File("$configurationFolder/starts")
    startDir.deleteRecursively()

    startDir.mkdirs()
    groupList.filter { it.listParticipants.size > 0 }.forEach { group ->
        val startGroupFile = File("$configurationFolder/starts/start_${group.groupName}.csv")
        val helper = group.listParticipants[0]
        csvWriter().writeAll(
            listOf(
                List(helper.toCSV().size) { if (it == 0) group.groupName else "" },
                helper.headerFormatCSV()
            ), startGroupFile, append = false
        )
        csvWriter().writeAll(group.listParticipants.map { it.toCSV() }, startGroupFile, append = true)
    }
    parseLogger.universalC(Colors.BLUE._name, "you created start protocols in folder $configurationFolder/starts", 'i' )
}

/** функция подбирает каждому участнику в каждой группе номер и время старта */
fun Event.setNumbersAndTime(groups:List<Group>) {
    parseLogger.universalC(Colors.BLUE._name, "the start time is selected for each participant", 'i' )
    val numberOfParticipants = groups.sumOf { it.listParticipants.size }
    var numbers = List(numberOfParticipants) { it + 1 }
    numbers = numbers.shuffled()
    var competitionsStart = Time(12, 0, 0)
    var index = 0
    groups.forEach { group ->
        val groupNum = "${groupList.indexOf(group) + 1}"
        val pref: Int = (groupNum.padEnd(groupNum.length + numberOfParticipants.toString().length, '0')).toInt()
        group.listParticipants.forEach { participant ->
            participant.setStart(pref + numbers[index++], Time(competitionsStart.timeInSeconds + 60))
            competitionsStart += Time(60)
        }
    }
}

