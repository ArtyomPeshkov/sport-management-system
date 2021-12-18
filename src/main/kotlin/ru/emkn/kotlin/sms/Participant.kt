package ru.emkn.kotlin.sms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck
import java.time.LocalDateTime

class ParticipantStart(val participant: Participant) : Participant(participant) {
    var number: Int = -1
        private set
    var startTime: Time = Time(0)
        private set


    fun setStart(num: Int, start: Time) {
        number = num
        startTime = start
    }

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

    fun toCSV(): List<String> =
        listOf("$number", surname, name, sex.toString(), "$yearOfBirth", team, rank, "$startTime")

    override fun toString(): String {
        return "Группа: ${Colors.BLUE._name}$wishGroup${Colors.PURPLE._name}; Номер: ${Colors.GREEN._name}$number${Colors.PURPLE._name}; Статус: ${Colors.YELLOW._name}$status${Colors.PURPLE._name}"
    }

    @Composable
    override fun <T> show(list: SnapshotStateList<T>, index: Int) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(this@ParticipantStart.number.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantStart.startTime.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantStart.participant.surname, modifier = Modifier.weight(1f))
            Text(this@ParticipantStart.participant.name, modifier = Modifier.weight(1f))
            Text(this@ParticipantStart.participant.wishGroup, modifier = Modifier.weight(1f))
        }
    }

}

open class Participant(
    sex: Sex,
    surname: String,
    name: String,
    yearOfBirth: Int,
    rank: String
) : Scrollable {
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
        if (yearOfBirth <= 1900 || yearOfBirth > LocalDateTime.now().year)
            throw UnexpectedValueException("Unreal date of birth: $yearOfBirth")
        this.sex = sex
        this.surname = surname
        this.name = name
        this.yearOfBirth = yearOfBirth
        this.rank = rank
    }

    constructor(participant: Participant) : this(
        participant.sex,
        participant.surname,
        participant.name,
        participant.yearOfBirth,
        participant.rank
    ) {
        this.wishGroup = participant.wishGroup
        this.team = participant.team
        this.status = participant.status
        this.points = participant.points
    }

    fun setParticipantStatus(stat: String) {
        status = stat
    }

    fun setPoints(points: Int) {
        require(points >= 0)
        this.points = points
    }

    fun setTeam(nameOfCollective: String) {
        if (this.team == "") {
            this.team = nameOfCollective
        }
    }

    fun setGroup(wishGroup: String) {
        this.wishGroup = wishGroup
    }

    @Composable
    override fun <T> show(list: SnapshotStateList<T>, index: Int) {
        //Тут нужна кнопочка по которой из participant вылезает прохождение им контрольных точек (условно при нажатии на номер участника, мы видим, как он прошёл дистанцию)
        var isOpened by remember { mutableStateOf(false) }



        Box(modifier = Modifier.fillMaxSize().clickable { isOpened = !isOpened }) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                //Где-то тут должно вызываться drop down menu
                Text(this@Participant.name, modifier = Modifier.fillMaxWidth(0.8f).padding(start = 5.dp))
                Button(onClick = { list.removeAt(index) }) { Text("Delete") }
            }
        }
    }


    override fun toString(): String {
        return "Группа: ${Colors.BLUE._name}$wishGroup${Colors.PURPLE._name}; Статус: ${Colors.YELLOW._name}$status${Colors.PURPLE._name}"
    }

}