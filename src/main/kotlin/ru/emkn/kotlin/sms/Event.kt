package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import log.printCollection
import log.universalC
import java.time.LocalDate


class Event(
    val name: String,
    val date: LocalDate,
    groupList: List<Group>,/*MutableList<Group>*/
    distanceList: Map<String, Distance>, /*MutableMap<String,String>*/
    //teams: List<Team>
) {
    var yearOfCompetition: Int = date.year
    var teamList: List<Team> = listOf() //список заявленных коллективов
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
    }

    constructor(
        name: String,
        date: LocalDate,
        groupList: List<Group>/*MutableList<Group>*/,
        distanceList: Map<String, Distance> /*MutableMap<String,String>*/,
        teams: List<Team>
    ) : this(name, date, groupList, distanceList) {
        if (teams.any { it.athleteList.isEmpty() })
            throw UnexpectedValueException("В коллективе нет участников")
        if (teams.isEmpty())
            throw UnexpectedValueException("Нет коллективов")
        teamList = teams
        setupGroups()
    }

    private fun setupGroups() {
        teamList.forEach { collective ->
            collective.athleteList.forEach { participant ->
                participant.chooseGroupByParams(groupList, yearOfCompetition
//                    participant.wishGroup,
//                    yearOfCompetition - participant.yearOfBirth,
//                    participant.sex
                )?.addParticipant(ParticipantStart(participant))
                    ?: parseLogger.universalC(
                        Colors.YELLOW._name,
                        "Для участника $participant не нашлось подходящей группы"
                    )
            }
        }
    }

    fun getDistanceList() = distanceList

//    private fun chooseGroupByParams(wishedGroup: String, age: Int, sex: Sex): Group? {
//        val wish = getGroupByName(wishedGroup, groupList)
//        if (wish != null && (sex == Sex.FEMALE || sex == wish.sex) && age >= wish.ageFrom && age <= wish.ageTo) {
//            return wish
//        }
//        return groupList.find { it.sex == sex && it.ageTo >= age && it.ageFrom <= age }
//    }

    fun getGroupsByDistance(distance: Distance): List<Group> {
        return groupList.filter { it.distance == distance }
    }

    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${teamList.size}"
    }

}