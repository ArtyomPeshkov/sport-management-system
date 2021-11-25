package ru.emkn.kotlin.sms


class Participant(
    group: String,
    sex: Sex,
    surname: String,
    name: String,
    yearOfBirth: Int,
    rank: String
) {
    val group: String
    val sex: Sex
    val surname: String
    val name: String
    val yearOfBirth: Int
    val rank: String

    init {
        this.group = group
        this.sex=sex
        this.surname = surname
        this.name=name
        this.yearOfBirth= yearOfBirth
        this.rank=rank
        /*TODO("Проверка корректности полей!!!")*/
    }

    private var number: Int = -1
    init {
    }
    fun setParticipantNumber()
    {
        number=1
    }

    override fun toString(): String {
        return "Группа: $group, Пол: $sex, Фамилия: $surname, Имя: $name, Год рождения: $yearOfBirth, Разряд: $rank "
    }

}