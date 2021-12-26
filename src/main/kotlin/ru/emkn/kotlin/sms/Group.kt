package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import exceptions.UnexpectedValueException
import exceptions.emptyNameCheck

open class Group(name: String, dist: Distance) : Scrollable {
    val groupName: String
    val distance: Distance
    var ageFrom: Int = 0
        private set
    var ageTo: Int = 0
        private set
    lateinit var sex: Sex
        private set

    init {
        emptyNameCheck(name, "Имя группы пустое")
        groupName = name
        distance = dist
    }

    constructor(group: Group) : this(group.groupName, group.distance) {
        ageFrom = group.ageFrom
        ageTo = group.ageTo
        sex = group.sex
    }

    val listParticipants: MutableList<ParticipantStart> = mutableListOf()

    /** добавляет переданного в функцию участника в список участников для данной группы */
    fun addParticipant(participant: ParticipantStart) {
        listParticipants.add(participant)
    }

    /** добавляет всех участников из переданного в функцию списка в список участников для данной группы */
    fun addParticipants(participants: Collection<ParticipantStart>) {
        listParticipants.addAll(participants)
    }

    /** выставляет возрастные и гендерные ограничения для данной группы */
    fun addDataWhenInitialise(ageFrom: Int, ageTo: Int, sex: Sex) {
        if (ageTo < 0 || ageFrom < 0 || ageFrom > ageTo)
            throw UnexpectedValueException("Проблема с возрастными ограничениями группы: Минимальный возраст = $ageFrom; Максимальный возраст = $ageTo")
        this.ageFrom = ageFrom
        this.ageTo = ageTo
        this.sex = sex
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
            Text(this@Group.groupName, modifier = Modifier.weight(1f))
            Text(this@Group.distance.name, modifier = Modifier.weight(1f))
            Text(this@Group.ageFrom.toString(), modifier = Modifier.weight(1f))
            Text(this@Group.ageTo.toString(), modifier = Modifier.weight(1f))
            if (isDeletable)Button(
                onClick = {
                    list.removeAt(index)
                    toDelete[0].removeIf {
                        it as ParticipantStart
                        it.wishGroup == this@Group.groupName
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Delete",
                    modifier = Modifier.fillMaxHeight(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

   fun createCSVHeader() = mutableListOf("Название", "Дистанция", "Пол", "ВозрастОт", "ВозрастДо")


    fun createCSVString() = mutableListOf(groupName, distance.name, "$sex", "$ageFrom", "$ageTo")


    override fun toString(): String {
        val s = StringBuilder("Название: $groupName\nДистанция: $distance\nСписок участников: $listParticipants\n")
        return s.toString()
    }
}

class GroupResults(group: Group):Group(group){

    @Composable
    override fun <T, E : Any> show(
        list: SnapshotStateList<T>,
        index: Int,
        isDeletable: Boolean,
        toDelete: List<SnapshotStateList<out E>>
    ) {
        var isOpened by remember { mutableStateOf(false) }
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
                Text(this@GroupResults.groupName, modifier = Modifier.weight(1f))
                Text(this@GroupResults.distance.name, modifier = Modifier.weight(1f))
                Text(this@GroupResults.ageFrom.toString(), modifier = Modifier.weight(1f))
                Text(this@GroupResults.ageTo.toString(), modifier = Modifier.weight(1f))
            }
            AnimatedVisibility(isOpened) {
                Column {
                    val list = toDelete[0][index] as List<ParticipantResult>
                    list.forEach {
                        Row{
                        Text(it.numberInList.toString(), modifier = Modifier.weight(1f))
                        Text(it.number.toString(), modifier = Modifier.weight(1f))
                        Text(it.surname, modifier = Modifier.weight(1f))
                        Text(it.name, modifier = Modifier.weight(1f))
                        Text(it.sex.toString(), modifier = Modifier.weight(1f))
                        Text(it.yearOfBirth.toString(), modifier = Modifier.weight(1f))
                        Text(it.team, modifier = Modifier.weight(1f))
                        Text(it.rank, modifier = Modifier.weight(1f))
                        Text(it.status, modifier = Modifier.weight(1f))
                        Text(if (it.place != -1)it.place.toString() else "", modifier = Modifier.weight(1f))
                        Text(it.otstav, modifier = Modifier.weight(1f))}
                    }
                }
            }
        }

    }
}