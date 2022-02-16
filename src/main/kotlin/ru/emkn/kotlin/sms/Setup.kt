package ru.emkn.kotlin.sms

import log.universalC

object Setup {
    /** распределяет участников из списка коллективов в список групп */
    fun setupGroups(collectiveList: List<Team>, groupList: List<Group>, yearOfCompetition: Int) {
        collectiveList.forEach { collective ->
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
}