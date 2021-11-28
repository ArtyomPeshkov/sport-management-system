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

fun chooseSex(sex: String): Sex {
    return when (sex) {
        "М", "M", "m", "м" -> Sex.MALE
        "Ж", "F", "ж", "f" -> Sex.FEMALE
        else -> throw SexException(sex)
    }
}

fun distancesParser(distances: File): Map<String, Distance> {
    val distanceStrings = csvReader().readAllWithHeader(distances)
    return distanceStrings.associate {
        Pair(
            it["Название"] ?: throw CSVFieldNamesException(distances.path),
            Distance(it, distances.path)
        )
    }
}

fun groupsParser(distanceList:Map<String, Distance>, groups: File,currentPhase:Phase): List<Group> {
    val groupStrings = csvReader().readAllWithHeader(groups)
    return groupStrings.map { group ->
        val distance = distanceList[group["Дистанция"]] ?: throw CSVFieldNamesException(groups.path)
        if (currentPhase==Phase.FIRST)
            Group(group, distance, groups.path)
        else
            Group(group["Название"] ?: throw CSVFieldNamesException(groups.path), distance)
    }.toSet().toList()
}

fun collectivesParser(applicationsFolder: File): List<Collective> {
    val applications =
        applicationsFolder.walk().toList().filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
    return applications.map { Collective(it.path) }
}

fun getCollectives(configurationFolder: List<File>,path: String) = collectivesParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
        ?: throw NotEnoughConfigurationFiles(path))

fun getDistances(configurationFolder: List<File>,path: String) = distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
    ?: throw NotEnoughConfigurationFiles(path))

fun getGroups(configurationFolder: List<File>,distanceList: Map<String,Distance>,path: String, currentPhase: Phase)=
    groupsParser(distanceList, configurationFolder.find { it.name.substringAfterLast('/') == "groups.csv" }
        ?: throw NotEnoughConfigurationFiles(path),currentPhase)

data class nameDate(val name:String,val date: LocalDate)

fun getNameAndDate(configurationFolder: List<File>,path: String): nameDate{
    val rows = csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
            ?: throw NotEnoughConfigurationFiles(path))
    return if (rows.size != 1)
        throw ProblemWithCSVException(path)
    else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
        nameDate( rows[0]["Название"] ?: throw CSVStringWithNameException(path),
        LocalDate.parse(rows[0]["Дата"], formatter))
    } else
        throw CSVStringWithNameException(path)
}

fun phase1(path: String)
{
    val configurationFolder=readFile(path).walk().toList()
    val distances = getDistances(configurationFolder,path)
    val groups = getGroups(configurationFolder,distances,path,Phase.FIRST)
    val collective = getCollectives(configurationFolder,path)
    val (name,date) = getNameAndDate(configurationFolder,path)
    val event = Event(name,date,groups,distances,collective)
    println(event.toString())
}

fun phase2(path:String)
{
    val configurationFolder=readFile(path).walk().toList()
    val distances = getDistances(configurationFolder,path)
    val groups = getGroups(configurationFolder,distances,path,Phase.SECOND)
}


fun main(args: Array<String>) {
    val path = "csvFiles/configuration"
    phase1(path)
}
