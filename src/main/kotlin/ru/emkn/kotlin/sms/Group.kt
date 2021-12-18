package ru.emkn.kotlin.sms

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

class Group(name: String, dist: Distance): Scrollable {
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
        if (dist.getPointsList().isEmpty())
            throw UnexpectedValueException("У дистанции  $dist нет контрольных точек")
        distance = dist
    }

    val listParticipants: MutableList<ParticipantStart> = mutableListOf()
    fun addParticipant(participant: ParticipantStart) {
        listParticipants.add(participant)
    }

    fun addParticipants(participants: Collection<ParticipantStart>) {
        listParticipants.addAll(participants)
    }

    fun addDataWhenInitialise(ageFrom: Int, ageTo: Int, sex: Sex) {
        if (ageTo < 0 || ageFrom < 0 || ageFrom > ageTo)
            throw UnexpectedValueException("Проблема с возрастными ограничениями группы: Минимальный возраст = $ageFrom; Максимальный возраст = $ageTo")
        this.ageFrom = ageFrom
        this.ageTo = ageTo
        this.sex = sex
    }

    fun toStringFull(): String {
        val s = StringBuilder(this.toString())
        s.append("Пол: $sex; Минимальный возраст: $ageFrom; Максимальный возраст: $ageTo")
        return s.toString()
    }

    @Composable
    override fun <T> show(list: SnapshotStateList<T>, index: Int) {
        var isOpened by remember { mutableStateOf(false) }

        val angle: Float by animateFloatAsState(
            targetValue = if (isOpened) 90F else 0F,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )

        Box(modifier = Modifier.fillMaxSize().clickable { isOpened = !isOpened }) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                Icon(
                    painter = painterResource("arrow.svg"),
                    contentDescription = null,
                    modifier = Modifier.width(10.dp).rotate(angle)
                )
                Text(this@Group.groupName, modifier = Modifier.fillMaxWidth(0.8f).padding(start = 5.dp))
                Button(onClick = { list.removeAt(index) }) { Text("Delete") }
            }
        }
    }

    override fun toString(): String {
        val s = StringBuilder("Название: $groupName\nДистанция: $distance\nСписок участников: $listParticipants\n")
        return s.toString()
    }
}