package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.NotEnoughConfigurationFiles
import exceptions.ProblemWithCSVException
import log.debugC
import java.io.File
import java.time.LocalDate


class Event(path: String) {
    //TODO("Вынести в отдельную функцию") Не получится, т.к. там нельзя определить поля
    val name: String
    val date: LocalDate
    val yearOfCompetition: Int
    var groupList: List<Group> = listOf()//список групп
    private var distanceList: Map<String, Distance> = mapOf()//список дистанций
    var collectiveList: List<Collective> = listOf()//список заявленных коллективов

    private val configurationFolder = readFile(path).walk().toList()

    init {
        parseLogger.debugC("Parsing event folder: $path")
//        val configurationFolder = readFile(path).walk().toList()
        distanceList = distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
            ?: throw NotEnoughConfigurationFiles(path))
        groupList =
            groupsParser(configurationFolder.find { it.name.substringAfterLast('/') == "groups.csv" }
                ?: throw NotEnoughConfigurationFiles(path))
        collectiveList =
            collectivesParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
                ?: throw NotEnoughConfigurationFiles(path))
        val rows =
            csvReader().readAllWithHeader(configurationFolder.find { it.name.substringAfterLast('/') == "event.csv" }
                ?: throw NotEnoughConfigurationFiles(path))
        if (rows.size != 1)
            throw ProblemWithCSVException(path)
        else if (rows[0]["Дата"] != null && rows[0]["Название"] != null && rows[0].size == 2) {
            name = rows[0]["Название"] ?: throw CSVStringWithNameException(path)
            date = LocalDate.parse(rows[0]["Дата"], formatter)
            yearOfCompetition = date.toString().substringBefore('-').toInt()
        } else
            throw CSVStringWithNameException(path)
        setupGroups()
        distanceList.forEach {
            setNumbersAndTime(getGroupsByDistance(it.value))
        }
        makeStartProtocols()
    }

    fun getGroupByName(name: String): Group? {
        return groupList.find { it.groupName == name }
    }

    fun getGroupsByDistance(distance: Distance): List<Group> {
        return groupList.filter { it.distance == distance }
    }

    fun chooseGroupByParams(wishedGroup: String, age: Int, sex: Sex): Group? {
        val wish = getGroupByName(wishedGroup)
        if (wish != null && (sex == Sex.FEMALE || sex == wish.sex) && age <= wish.ageTo) {
            return wish
        }
        return groupList.find { it.sex == sex && it.ageTo >= age && it.ageFrom <= age }
    }

    fun groupsParser(groups: File): List<Group> {
        val groupStrings = csvReader().readAllWithHeader(groups)
        return groupStrings.map { group ->
            val distance = distanceList[group["Дистанция"]] ?: throw CSVFieldNamesException(groups.path)
            Group(group, distance, groups.path)
        }.toSet().toList()
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

    fun collectivesParser(applicationsFolder: File): List<Collective> {
        val applications =
            applicationsFolder.walk().toList().filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
        return applications.map { Collective(it.path) }
    }

    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${collectiveList.size}"
    }

    //TODO(что-то с папками)
    fun makeStartProtocols() {
        val generalFile = File("csvFiles/starts/start_general.csv")
        val generalLines = mutableListOf<List<String>>()
        groupList.filter { it.listParticipants.size > 0 }.forEach { group ->
            val file = File("csvFiles/starts/start_${group.groupName}.csv")
            csvWriter().writeAll(listOf(List(6) { if (it == 0) group.groupName else "" }), file, append = false)
            csvWriter().writeAll(group.listParticipants.map { it.toCSV() }, file, append = true)
            generalLines.addAll(group.listParticipants.map { it.toCSV() })
        }
        csvWriter().writeAll(generalLines, generalFile)
    }
}

fun Event.setNumbersAndTime(groups: List<Group>) {
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


fun Event.setupGroups() {
    collectiveList.forEach { collective ->
        collective.athleteList.forEach { participant ->
            chooseGroupByParams(
                participant.wishGroup,
                yearOfCompetition - participant.yearOfBirth,
                participant.sex
            )?.addParticipant(participant)
                ?: parseLogger.debugC("Для участника $participant не нашлось подходящей группы")
        }
    }
}
