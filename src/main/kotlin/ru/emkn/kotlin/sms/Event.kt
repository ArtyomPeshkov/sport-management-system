package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import exceptions.ProblemWithCSVException
import log.debugC
import java.io.File
import java.time.*


class Event {
    val name: String
    val date: LocalDate
    var groupList: List<Group> = listOf()//список групп
    private var distanceList: List<Distance> = listOf()//список дистанций
    var collectiveList: List<Collective> = listOf()//список заявленных коллективов


    fun getDistanceByName(name: String): Distance? {
        return distanceList.find { it.name == name }
    }

    fun groupsParser(groups: File): List<Group> {
        val groupStrings = csvReader().readAllWithHeader(groups)
        return groupStrings.map { group ->
            Group(
                group["Название"] ?: throw CSVFieldNamesException(groups.path),
                distanceList.find { dist -> dist.name == group["Дистанция"] }
                    ?: throw CSVFieldNamesException(groups.path))
        }
    }

    fun distancesParser(distances: File): List<Distance> {
        val distanceStrings = csvReader().readAllWithHeader(distances)
        return distanceStrings.map { Distance(it, distances.toString()) }

    }


    fun collectivesParser(applicationsFolder:File):List<Collective>
    {
        val applications = applicationsFolder.walk().toList().filter { ".*\\.csv".toRegex().matches(it.path.substringAfterLast('/')) }
        return applications.map{ Collective(it.path) }
    }


    constructor (path: String) {
        parseLogger.debugC("Parsing event folder: $path")
        val configurationFolder = readFile(path).walk().toList()
        distanceList = distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
            ?: throw NotEnoughConfigurationFiles(path))
        groupList =
            groupsParser(configurationFolder.find { it.name.substringAfterLast('/') == "groupsAndDistances.csv" }
                ?: throw NotEnoughConfigurationFiles(path))
        collectiveList =collectivesParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
            ?: throw NotEnoughConfigurationFiles(path))

        //TODO("Вынести в отдельную функцию")
        val rows =
            csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
                ?: throw NotEnoughConfigurationFiles(path))
        if (rows.size != 1)
            throw ProblemWithCSVException(path)
        else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
            name = rows[0]["Название"] ?: ""
            date = LocalDate.parse(rows[0]["Дата"], formatter)
        } else
            throw CSVStringWithNameException(path)
    }


    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${collectiveList.size}"
    }
}