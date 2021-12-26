package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import log.printCollection
import log.universalC
import java.time.LocalDate


class Event(
    var name: String,
    var date: LocalDate,
    groupList: List<Group>,/*MutableList<Group>*/
    distanceList: Map<String, Distance>, /*MutableMap<String,String>*/
    //teams: List<Team>
) {
    var yearOfCompetition: Int = date.year
    var teamList: List<Team> = listOf() //список заявленных коллективов
    val groupList: List<Group>
    private val distanceList: Map<String, Distance>

    init {
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
        teamList = teams
        setupGroups()
    }

    /** распределяет участников из списка коллективов в список групп */
    private fun setupGroups() {
        teamList.forEach { collective ->
            collective.athleteList.forEach { participant ->
                participant.chooseGroupByParams(
                    groupList, yearOfCompetition
                )?.addParticipant(ParticipantStart(participant))

                    ?: parseLogger.universalC(
                        Colors.YELLOW._name,
                        "Для участника $participant не нашлось подходящей группы"
                    )
            }
        }
    }

    fun getDistanceList() = distanceList

    /** функция подбирает подходящую участнику группу, исходя из его возраста, пола и желаемой группы */
    private fun chooseGroupByParams(wishedGroup: String, age: Int, sex: Sex): Group? {
        val wish = getGroupByName(wishedGroup, groupList)
        if (wish != null && (sex == Sex.FEMALE || sex == wish.sex) && age >= wish.ageFrom && age <= wish.ageTo) {
            return wish
        }
        return groupList.find { it.sex == sex && it.ageTo >= age && it.ageFrom <= age }
    }

    /** позволяет найти группы, проходящие определенную дистанцию */
    fun getGroupsByDistance(distance: Distance): List<Group> {
        return groupList.filter { it.distance == distance }
    }

    override fun toString(): String {
        return "Название:$name, дата: $date, количество групп: ${groupList.size}, количество дистанций: ${distanceList.size}, количество коллективов: ${teamList.size}"
    }

}