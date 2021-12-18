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

class Team(name: String) : Scrollable {
    val name: String
    var athleteList: MutableList<Participant> = mutableListOf()
    var points = 0

    init {
        emptyNameCheck(name, "Пустое имя коллектива")
        this.name = name
    }

    fun addParticipant(participant: Participant) {
        athleteList.add(participant)
        if (participant.points < 0)
            throw UnexpectedValueException("Количество очков у участника ${participant.number} отрицательно")
        points += participant.points
    }

    fun addParticipants(participants: Collection<Participant>) {
        athleteList.addAll(participants)
    }

    @Composable
    override fun <T> show(list: SnapshotStateList<T>, index: Int) {
        var isOpened by remember { mutableStateOf(false) }
        val listOfParticipant = athleteList.toMutableStateList()

        val angle: Float by animateFloatAsState(
            targetValue = if (isOpened) 90F else 0F,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
        Column {
            Box(modifier = Modifier.fillMaxSize().clickable { isOpened = !isOpened }) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                    Icon(
                        painter = painterResource("arrow.svg"),
                        contentDescription = null,
                        modifier = Modifier.width(10.dp).rotate(angle)
                    )
                    Text(this@Team.name, modifier = Modifier.fillMaxWidth(0.8f).padding(start = 5.dp))
                    Button(onClick = { list.removeAt(index) }) { Text("Delete") }
                }
            }

            AnimatedVisibility(isOpened) {
                Column {
                    listOfParticipant.forEachIndexed { i, it ->
                        Row {
                            Text(it.name)
                            Button(
                                onClick = {
                                    listOfParticipant.removeAt(i)
                                    athleteList.removeAt(i)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = Color.Red,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "Delete",
                                    modifier = Modifier.fillMaxHeight(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}