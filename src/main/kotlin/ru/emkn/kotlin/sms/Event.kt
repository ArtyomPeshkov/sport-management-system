package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.*
import log.debugC
import java.io.File
import java.lang.Math.pow
import java.time.*
import kotlin.random.Random


class Event {
    val name: String
    val date: LocalDate
    val yearOfCompetition: Int
    var groupList: List<Group> = listOf()//список групп
    private var distanceList: List<Distance> = listOf()//список дистанций
    var collectiveList: List<Collective> = listOf()//список заявленных коллективов

    constructor (path: String) {
        parseLogger.debugC("Parsing event folder: $path")
        val configurationFolder = readFile(path).walk().toList()
        distanceList = distancesParser(configurationFolder.find { it.name.substringAfterLast('/') == "distances.csv" }
            ?: throw NotEnoughConfigurationFiles(path))
        groupList =
            groupsParser(configurationFolder.find { it.name.substringAfterLast('/') == "groups.csv" }
                ?: throw NotEnoughConfigurationFiles(path))
        collectiveList =
            collectivesParser(configurationFolder.find { it.name.substringAfterLast('/') == "applications" }
                ?: throw NotEnoughConfigurationFiles(path))


        //TODO("Вынести в отдельную функцию")
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
            setNumbersAndTime(getGroupsByDistance(it))
        }
    }

    fun getDistanceByName(name: String): Distance {
        return distanceList.find { it.name == name } ?: throw  UnexpectedValueException(name)
    }

    fun getGroupByName(name: String): Group? {
        return groupList.find { it.groupName == name }
    }

    fun getGroupsByDistance(distance: Distance): List<Group> {
        return groupList.filter { it.distance== distance}
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
            val distance = getDistanceByName(group["Дистанция"] ?: throw CSVFieldNamesException(groups.path))
            Group(group, distance, groups.path)
        }.toSet().toList()
    }

    fun distancesParser(distances: File): List<Distance> {
        val distanceStrings = csvReader().readAllWithHeader(distances)
        return distanceStrings.map { Distance(it, distances.path) }

    }


    fun collectivesParser(applicationsFolder: File): List<Collective> {
        val applications =
            applicationsFolder.walk().toList().filter { ".*[.]csv".toRegex().matches(it.path.substringAfterLast('/')) }
        return applications.map { Collective(it.path) }
    }

    fun setupGroups() {
        collectiveList.forEach {
            it.athleteList.forEach { participant ->
                chooseGroupByParams(
                    participant.wishGroup,
                    yearOfCompetition - participant.yearOfBirth,
                    participant.sex
                )?.addParticipant(participant) ?: parseLogger.debugC("Для участника $participant не нашлось подходящей группы")
            }
        }
    }

    fun setNumbersAndTime(groups:List<Group>)
    {
        val numberOfParticipants = groups.sumOf { it.listParticipants.size }
        val numbers = mutableListOf<Int>()
        while (numbers.size < numberOfParticipants) {
            numbers += Random.nextInt(1,numberOfParticipants+1)
            numbers.toSet().toMutableList()
        }
        var competitionsStart = Time(12,0,0)
        var index=0
        groups.forEach {
            val groupNum="${groupList.indexOf(it)+1}"
            val pref:Int = (groupNum.padEnd(groupNum.length+numberOfParticipants.toString().length,'0')).toInt()
            it.listParticipants.forEach{
                it.setStart(pref+numbers[index++],Time(competitionsStart.timeInSeconds+60))
                competitionsStart += Time(60)
            }
        }
    }
    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${collectiveList.size}"
    }
}