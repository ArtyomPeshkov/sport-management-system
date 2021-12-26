package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.UnexpectedValueException
import log.universalC
import java.io.File

fun setStatusForAllParticipants(
    groups: List<Group>,
    distances: Map<String, Distance>,
    participantDistanceWithTime: Map<Int, List<ControlPointWithTime>>
) {
    groups.forEach { group ->
        group.listParticipants.forEach { participant ->
            participant.setParticipantStatus(
                distances[group.distance.name]?.checkProtocolPointsCorrectness(
                    participant,
                    participantDistanceWithTime
                ) ?: throw UnexpectedValueException(group.distance.name)
            )
        }
    }
}


/** функция, используя собранные ранее данные, создает протоколы результатов для групп */
fun makeResultProtocols(groups: List<Group>, configurationFolder: String) {

    //Сделать так, чтобы оно вернуло список ParticipantResult для каждой группы

    parseLogger.universalC(Colors.BLUE._name, "making result protocols", 'i')
    val resultDir = File("$configurationFolder/results/")
    resultDir.deleteRecursively()
    resultDir.mkdirs()
    groups.filter { it.listParticipants.size > 0 }.forEach { group ->
        val resultGroupFile = File("${resultDir.path}/result_${group.groupName}.csv")
        val helper = group.listParticipants[0]
        csvWriter().writeAll(
            listOf(
                List(helper.headerFormatCSVResult().size) { if (it == 0) group.groupName else "" },
                helper.headerFormatCSVResult()
            ), resultGroupFile, append = false
        )
        val (participants, deletedParticipants) = group.listParticipants.partition { it.status != "Снят" }
        val result: List<ParticipantStart> = participants.sortedBy { Time(it.status).timeInSeconds } + deletedParticipants
        var number = 1
        var place = 1

        fun setPlace(): String {
            return if (number > 2 && result[number - 3].status == result[number - 2].status && result[number - 2].status != "Снят")
                (place - 1).toString()
            else if (result[number - 2].status != "Снят")
                (place++).toString()
            else
                ""
        }

        fun setTime(participant: Participant): String {
            return if (place != 2 && participant.status != "Снят") "+" + (Time(participant.status) - Time(result[0].status)) else ""
        }

        csvWriter().writeAll(result.map {
            listOf(
                number++.toString(),
                it.number,
                it.surname,
                it.name,
                it.sex,
                it.yearOfBirth,
                it.team,
                it.rank,
                it.status,
                setPlace(),
                setTime(it)
            )
        }, resultGroupFile, append = true)
    }
}


/** функция создает протокол результатов для команд */
fun generateResultProtocolForCollectives(teams: MutableList<Team>,configurationFolder:String) {
    val file = File("csvFiles/configuration/teamsResults.csv")
    file.writeText("")
    teams.forEach {
        csvWriter().writeAll(
            listOf(
                listOf("Коллектив", "Баллы"),
                listOf(it.name, "${it.points}"),
                listOf(""),
                listOf("Участник", "Баллы")
            ), file, append = true
        )
        it.athleteList.forEach {
            csvWriter().writeAll(
                listOf(listOf("${it.surname} ${it.name}", "${it.points}")),
                file,
                append = true
            )
        }
        csvWriter().writeAll(
            listOf(
                listOf(""),
                listOf("<-------------------------------------------------------->"),
                listOf("")
            ), file, append = true
        )
    }
}