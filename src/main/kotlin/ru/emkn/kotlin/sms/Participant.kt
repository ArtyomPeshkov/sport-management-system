package ru.emkn.kotlin.sms

import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck
import java.time.LocalDateTime

class Participant(
    sex: Sex,
    surname: String,
    name: String,
    yearOfBirth: Int,
    rank: String
) {
    var wishGroup: String = ""
        private set
    val sex: Sex
    val surname: String
    val name: String
    val yearOfBirth: Int
    val rank: String
    var team: String = ""
        private set
    var points: Int = 0
        private set
    var status: String = ""

    init {
        emptyNameCheck(name, "Пустое имя участника обнаружено")
        emptyNameCheck(surname, "Пустая фамилия участника обнаружена")
        if (yearOfBirth <= 1900 || yearOfBirth>LocalDateTime.now().year)
            throw UnexpectedValueException("Unreal date of birth: $yearOfBirth")
        this.sex = sex
        this.surname = surname
        this.name = name
        this.yearOfBirth = yearOfBirth
        this.rank = rank
    }

    var number: Int = -1
        private set
    var startTime: Time = Time(0)
        private set

    fun setStart(num: Int, start: Time) {
        number = num
        startTime = start
    }

    fun setParticipantStatus(stat: String) {
        status = stat
    }

    fun setPoints(points: Int) {
        require(points>=0)
        this.points = points
    }

    fun setTeam(nameOfCollective: String) {
        if (this.team == "") {
            this.team = nameOfCollective
        }
    }

    fun setGroup(wishGroup: String)
    {
        this.wishGroup=wishGroup
    }

    override fun toString(): String {
        return "Группа: ${Colors.BLUE._name}$wishGroup${Colors.PURPLE._name}; Номер: ${Colors.GREEN._name}$number${Colors.PURPLE._name}; Статус: ${Colors.YELLOW._name}$status${Colors.PURPLE._name}"
    }


    fun fullToString(): String {
        return this.toString() + "Пол: $sex; Год рождения: $yearOfBirth; Разряд: $rank"
    }

    fun toCSV(): List<String> =
        listOf("$number", surname, name, sex.toString(), "$yearOfBirth", team, rank, "$startTime")

    fun headerFormatCSV() = listOf("Номер", "Фамилия", "Имя", "Пол", "Г.р.", "Коллектив", "Разр.", "Стартовое время")
    fun headerFormatCSVResult() = listOf(
        "Порядковый номер",
        "Номер",
        "Фамилия",
        "Имя",
        "Пол",
        "Г.р.",
        "Коллектив",
        "Разр.",
        "Результат",
        "Место",
        "Отставание"
    )

    fun toCSVStartTime(): List<String> = listOf("$number", "$startTime")
}