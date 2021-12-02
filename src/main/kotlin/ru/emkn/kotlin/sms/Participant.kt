package ru.emkn.kotlin.sms

class Participant {
    val wishGroup: String
    val sex: Sex
    val surname: String
    val name: String
    val yearOfBirth: Int
    val rank: String
    var collective: String = ""
        private set
    var points: Int = 0
        private set
    var status: String = ""

    constructor(
        group: String,
        sex: Sex,
        surname: String,
        name: String,
        yearOfBirth: Int,
        rank: String
    ) {
        this.wishGroup = group
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

    fun setParticipantStatus(stat:String)
    {
        status=stat
    }

    fun setPoints(points: Int) {
        this.points = points
    }

    fun setCollective(nameOfCollective: String) {
        if (this.collective == "") { //поменять коллектив можно только один раз
            this.collective = nameOfCollective
        }
    }

    override fun toString(): String
    {
        return "Группа: ${Colors.BLUE._name}$wishGroup${Colors.PURPLE._name}; Номер: ${Colors.GREEN._name}$number${Colors.PURPLE._name}; Статус: ${Colors.YELLOW._name}$status${Colors.PURPLE._name}"
    }


    fun fullToString(): String {
        return this.toString() + "Пол: $sex; Год рождения: $yearOfBirth; Разряд: $rank"
    }

    fun toCSV(): List<String> = listOf("$number", surname, name, sex.toString(), "$yearOfBirth",collective, rank, "$startTime")
    fun headerFormatCSV() = listOf("Номер", "Фамилия", "Имя", "Пол"  ,"Г.р.", "Коллектив", "Разр.", "Стартовое время")
    fun headerFormatCSVResult() = listOf("Порядковый номер","Номер", "Фамилия", "Имя", "Пол"  ,"Г.р.", "Коллектив", "Разр.", "Результат","Место","Отставание")
    fun toCSVStartTime(): List<String> = listOf("$number","$startTime")
}