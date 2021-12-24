package ru.emkn.kotlin.sms

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

class Group(name: String, dist: Distance) : Scrollable {
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
            Button(
                onClick = {
                    list.removeAt(index)
                    toDelete[0].removeIf{ it as ParticipantStart
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