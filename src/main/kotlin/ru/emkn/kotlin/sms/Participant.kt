package ru.emkn.kotlin.sms

import java.awt.Point


class Participant {
    val group: String
    val sex: Sex
    val surname: String
    val name: String
    val yearOfBirth: Int
    val rank: String
    var points: Int = 0
        private set

    constructor(
        group: String,
        sex: Sex,
        surname: String,
        name: String,
        yearOfBirth: Int,
        rank: String
    ) {
        this.group = group
        this.sex = sex
        this.surname = surname
        this.name = name
        this.yearOfBirth = yearOfBirth
        this.rank = rank
    }

    private var number: Int = -1

    fun setParticipantNumber() //Номер после жеребьёвки
    {
        number = 1
    }

    fun setPoints(point: Int) {
        this.points = points
    }

    override fun toString(): String {
        return "Группа: $group, Пол: $sex, Фамилия: $surname, Имя: $name, Год рождения: $yearOfBirth, Разряд: $rank "
    }

}