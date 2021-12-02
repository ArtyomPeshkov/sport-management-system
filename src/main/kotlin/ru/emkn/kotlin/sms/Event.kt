package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import log.printCollection
import log.universalC
import java.io.File
import java.time.LocalDate

class Event(groups: List<Group>, distances: Map<String, Distance>) {
    var name: String = ""
    var date: LocalDate = LocalDate.parse("01.01.2021", formatter)
    var yearOfCompetition: Int = date.year
    var groupList: List<Group> = listOf() //список групп
    private var distanceList: Map<String, Distance> = mapOf() //список дистанций
    var collectiveList: List<Collective> = listOf() //список заявленных коллективов


    constructor(
        name: String,
        date: LocalDate,
        groups: List<Group>,
        distances: Map<String, Distance>,
        collectives: List<Collective>
    ) : this(groups, distances) {
        this.name = name;
        this.date = date
        collectiveList = collectives
        setupGroups()
        parseLogger.printCollection(groupList, Colors.PURPLE._name)
        distanceList.forEach {
            setNumbersAndTime(getGroupsByDistance(it.value))
        }
        makeStartProtocols()
    }

    init {
        groupList = groups
        distanceList = distances
    }

    fun getGroupsByDistance(distance: Distance): List<Group> {
        return groupList.filter { it.distance == distance }
    }

    fun chooseGroupByParams(wishedGroup: String, age: Int, sex: Sex): Group? {
        val wish = getGroupByName(wishedGroup, groupList)
        if (wish != null && (sex == Sex.FEMALE || sex == wish.sex) && age >= wish.ageFrom && age <= wish.ageTo) {
            return wish
        }
        return groupList.find { it.sex == sex && it.ageTo >= age && it.ageFrom <= age }
    }

    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${collectiveList.size}"
    }

}

fun Event.makeStartProtocols() {
    val startDir = File("csvFiles/configuration/starts/")
    startDir.mkdirs()
    //val generalFile = File("csvFiles/starts/start_general.csv")
    //val generalLines = mutableListOf<List<String>>()
    groupList.filter { it.listParticipants.size > 0 }.forEach { group ->
        val startGroupFile = File("csvFiles/configuration/starts/start_${group.groupName}.csv")
        val helper = group.listParticipants[0]
        csvWriter().writeAll(
            listOf(
                List(helper.toCSV().size) { if (it == 0) group.groupName else "" },
                helper.headerFormatCSV()
            ), startGroupFile, append = false
        )
        csvWriter().writeAll(group.listParticipants.map { it.toCSV() }, startGroupFile, append = true)

        // generalLines.addAll(group.listParticipants.map { it.toCSV() })
    }
    //  csvWriter().writeAll(generalLines, generalFile)
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
                ?: parseLogger.universalC(
                    Colors.YELLOW._name,
                    "Для участника $participant не нашлось подходящей группы"
                )
        }
    }
}
