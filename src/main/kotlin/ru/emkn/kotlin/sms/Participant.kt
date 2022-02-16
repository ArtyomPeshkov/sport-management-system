package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck
import java.time.LocalDateTime

open class ParticipantStart(val participant: Participant) : Participant(participant) {

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
        return "Номер: $number " + super.toString()
    }

    @Composable
    override fun <T, E : Any> show(
        list: SnapshotStateList<T>,
        index: Int,
        isDeletable: Boolean,
        toDelete: List<SnapshotStateList<out E>>
    ) {
        var isOpened by remember { mutableStateOf(false) }
        var typeOfSort by remember { mutableStateOf(0) }
        val angle: Float by animateFloatAsState(
            targetValue = if (isOpened) 90F else 0F,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
        Column {
            Row(
                modifier = Modifier.fillMaxHeight().clickable { isOpened = !isOpened },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    painter = painterResource("arrow.svg"),
                    contentDescription = null,
                    modifier = Modifier.width(10.dp).rotate(angle)
                )
                Text(this@ParticipantStart.number.toString(), modifier = Modifier.weight(1f))
                Text(this@ParticipantStart.startTime.toString(), modifier = Modifier.weight(1f))
                Text(this@ParticipantStart.participant.surname, modifier = Modifier.weight(1f))
                Text(this@ParticipantStart.participant.name, modifier = Modifier.weight(1f))
                Text(this@ParticipantStart.participant.wishGroup, modifier = Modifier.weight(1f))
                if (isDeletable) Button(onClick = {
                    list.removeAt(index)
                    toDelete[0].forEach { group ->
                        group as Group
                        group.listParticipants.removeIf { it == this@ParticipantStart }
                    }
                }) { Text("Delete") }
            }

            AnimatedVisibility(isOpened) {
                Column {
                    var cp = toDelete[0][index] as List<ControlPointWithTime> //Да я отвечаю все норм
                    cp = when(typeOfSort) {
                        1 -> cp.sortedBy { it.point.name }
                        2 -> cp.sortedByDescending { it.point.name }
                        3 -> cp.sortedBy { it.time.timeInSeconds }
                        4 -> cp.sortedByDescending { it.time.timeInSeconds }
                        else -> cp
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Button(onClick = { typeOfSort = 1 }, modifier = Modifier.weight(1f)) {
                            Text(
                                "По имени ^"
                            )
                        }
                        Button(
                            onClick = { typeOfSort = 2 },
                            modifier = Modifier.weight(1f)
                        ) { Text("По имени v") }
                        Button(
                            onClick = { typeOfSort = 3 },
                            modifier = Modifier.weight(1f)
                        ) { Text("По времени ^") }
                        Button(
                            onClick = { typeOfSort = 4 },
                            modifier = Modifier.weight(1f)
                        ) { Text("По времени v") }
                    }
                    cp.forEach {
                        Text("${it.point.name}: ${it.time}")
                    }
                }
            }
        }
    }

}

class ParticipantStartProtocol(participant: ParticipantStart) : ParticipantStart(participant.participant) {

    init {
        this.setStart(participant.number, participant.startTime)
    }

    @Composable
    override fun <T, E : Any> show(
        list: SnapshotStateList<T>,
        index: Int,
        isDeletable: Boolean,
        toDelete: List<SnapshotStateList<out E>>
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(this@ParticipantStartProtocol.number.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantStartProtocol.startTime.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantStartProtocol.participant.surname, modifier = Modifier.weight(1f))
            Text(this@ParticipantStartProtocol.participant.name, modifier = Modifier.weight(1f))
            Text(this@ParticipantStartProtocol.participant.wishGroup, modifier = Modifier.weight(1f))
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

    /** функция подбирает подходящую участнику группу, исходя из его возраста, пола и желаемой группы */
    fun chooseGroupByParams(groupList: List<Group>, yearOfCompetition: Int): Group? {
        val age = yearOfCompetition - yearOfBirth
        val wish = getGroupByName(wishGroup, groupList)
        if (wish != null && (sex == Sex.FEMALE || sex == wish.sex) && age >= wish.ageFrom && age <= wish.ageTo) {
            return wish
        }
        return groupList.find { it.sex == sex && it.ageTo >= age && it.ageFrom <= age }
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
    override fun <T, E : Any> show(
        list: SnapshotStateList<T>,
        index: Int,
        isDeletable: Boolean,
        toDelete: List<SnapshotStateList<out E>>
    ) {
        //Тут нужна кнопочка по которой из participant вылезает прохождение им контрольных точек (условно при нажатии на номер участника, мы видим, как он прошёл дистанцию)
        var isOpened by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxSize().clickable { isOpened = !isOpened }) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                //Где-то тут должно вызываться drop down menu
                Text(this@Participant.name, modifier = Modifier.fillMaxWidth(0.8f).padding(start = 5.dp))
                Button(onClick = {
                    list.removeAt(index)
                    toDelete[0].removeIf {
                        it as Group
                        it.listParticipants.removeIf { it.participant == this@Participant }
                    }
                }) { Text("Delete") }
            }
        }
    }


    override fun toString(): String {
        return "Фамилия: $surname  Имя:$name  Статус: $status Год рождения: $yearOfBirth ||||| "
    }

}

class ParticipantResult(participant: ParticipantStart,val numberInList: Int,val place: Int,val otstav: String) : ParticipantStart(participant.participant) {
    init {
        setStart(participant.number,participant.startTime)
    }

    fun createCSVHeaderRes() = mutableListOf(
        listOf(
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
    )


    fun createCSVStringRes(): MutableList<List<String>> = mutableListOf(
        listOf(
            "$numberInList", "$number", surname,
            name, "$sex", "$yearOfBirth",
            team, rank, status, "$place", otstav
        )
    )

    @Composable
    override fun <T, E : Any> show(
        list: SnapshotStateList<T>,
        index: Int,
        isDeletable: Boolean,
        toDelete: List<SnapshotStateList<out E>>
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(this@ParticipantResult.numberInList.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.number.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.surname, modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.name, modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.sex.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.yearOfBirth.toString(), modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.team, modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.rank, modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.status, modifier = Modifier.weight(1f))
            Text(if (this@ParticipantResult.place != -1)this@ParticipantResult.place.toString() else "", modifier = Modifier.weight(1f))
            Text(this@ParticipantResult.otstav, modifier = Modifier.weight(1f))
        }
    }

}