package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.UnexpectedValueException
import log.printCollection
import log.universalC
import java.io.File
import java.time.LocalDate


class Event(
    val name: String,
    val date: LocalDate,
    groupList: List<Group>/*MutableList<Group>*/,
    distanceList: Map<String, Distance> /*MutableMap<String,String>*/,
    collectives: List<Collective>
) {
    var yearOfCompetition: Int = date.year
    var collectiveList: List<Collective> = listOf() //список заявленных коллективов
    val groupList: List<Group>
    private val distanceList: Map<String, Distance>

    init {
        if (groupList.isEmpty())
            throw UnexpectedValueException("Пустой список групп у события")
        if (distanceList.isEmpty())
            throw UnexpectedValueException("Пустой список дистанций у события")
        this.groupList = groupList
        this.distanceList = distanceList
        parseLogger.printCollection(groupList, Colors.PURPLE._name)
        if (collectives.any { it.athleteList.isEmpty() })
            throw UnexpectedValueException("В коллективе нет участников")
        if (collectives.isEmpty())
            throw UnexpectedValueException("Нет коллективов")
        setupCollectives(collectives)
        setupGroups()
    }

    private fun setupGroups() {
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

    fun getDistanceList() = distanceList

    private fun chooseGroupByParams(wishedGroup: String, age: Int, sex: Sex): Group? {
        val wish = getGroupByName(wishedGroup, groupList)
        if (wish != null && (sex == Sex.FEMALE || sex == wish.sex) && age >= wish.ageFrom && age <= wish.ageTo) {
            return wish
        }
        return groupList.find { it.sex == sex && it.ageTo >= age && it.ageFrom <= age }
    }

    private fun setupCollectives(collectives: List<Collective>) {
        collectiveList = collectives
    }

    fun getGroupsByDistance(distance: Distance): List<Group> {
        return groupList.filter { it.distance == distance }
    }

    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${collectiveList.size}"
    }

}