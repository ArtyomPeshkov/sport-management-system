package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import exceptions.ProblemWithCSVException
import log.debugC
import java.io.File
import java.time.*


class Event(path: String) {
    val name: String
    val date: LocalDate
    var groupList: List<Group> = listOf()//список групп
    private var distanceList: List<Distance> = listOf()
    fun getDistanceByName(name: String): Distance? {
        return distanceList.find { it.name == name }
    }

    fun groupsParser(groups: File): List<Group> {
        val groupStrings = csvReader().readAllWithHeader(groups)
        return groupStrings.map { group ->
            Group(
                group["Название"] ?: throw CSVFieldNamesException(groups.path),
                distanceList.find { dist -> dist.name == group["Дистанция"] } ?: throw CSVFieldNamesException(groups.path))
        }
    }

    fun distancesParser(distances: File): List<Distance> {
        val distanceStrings = csvReader().readAllWithHeader(distances)
        return distanceStrings.map { Distance(it, distances.toString()) }

    }

    fun eventParser(path: String): Pair<String, LocalDate> {
        parseLogger.debugC("Parsing event folder: $path")
        val configurationFolder = readFile(path).walk().toList()
        distanceList = distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
            ?: throw NotEnoughConfigurationFiles(path))
        groupList = groupsParser(configurationFolder.find { it.name.substringAfterLast('/') == "groupsAndDistances.csv" }
            ?: throw NotEnoughConfigurationFiles(path))

         val rows = csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
             ?: throw NotEnoughConfigurationFiles(path))
         if (rows.size != 1)
             throw ProblemWithCSVException(path)
         else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
             return Pair(rows[0]["Название"] ?: "", LocalDate.parse(rows[0]["Дата"], formatter))
         } else
             throw CSVStringWithNameException(path)
    }


    var collectiveList: MutableList<Collective> = mutableListOf()//список заявленных коллективов

    init {
        eventParser(path).let {
            name = it.first
            date = it.second
        }
    }

    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}"
    }
}