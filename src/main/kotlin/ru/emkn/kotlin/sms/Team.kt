package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
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

    /** добавляет переданного в функцию участника в список участников для данной команды (коллектива) */
    fun addParticipant(participant: Participant) {
        athleteList.add(participant)
        if (participant.points < 0)
            throw UnexpectedValueException("Количество очков у участника ${participant.name} ${participant.surname} отрицательно")
        points += participant.points
    }

    /** добавляет всех участников из переданного в функцию списка в список участников для данной команды (коллектива) */
    fun addParticipants(participants: Collection<Participant>) {
        athleteList.addAll(participants)
    }

    fun createCSVHeader() = mutableListOf(
        listOf(name, "", "", "", "", ""),
        listOf("Группа", "Фамилия", "Имя", "Пол", "Г.р.", "Разр.")
    )

    fun createCSVStrings(): MutableList<List<String>> {
        val res = mutableListOf<List<String>>()
        athleteList.forEach {
            res.add(listOf(it.wishGroup, it.surname, it.name, it.sex.toString(), it.yearOfBirth.toString(), it.rank))
        }
        return res
    }

    @Composable
    override fun <T, E : Any> show(
        list: SnapshotStateList<T>,
        index: Int,
        isDeletable: Boolean,
        toDelete: List<SnapshotStateList<out E>>
    ) {
        var isOpened by remember { mutableStateOf(false) }
        var isVisible by remember { mutableStateOf(false) }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        painter = painterResource("arrow.svg"),
                        contentDescription = null,
                        modifier = Modifier.width(10.dp).rotate(angle)
                    )
                    Text(this@Team.name, modifier = Modifier.fillMaxWidth(0.8f).padding(start = 5.dp))
                    Button(
                        onClick = {
                            isVisible = false
                            list.removeAt(index)
                            toDelete[0].forEach { group ->
                                group as Group
                                group.listParticipants.removeIf {
                                    it.team == this@Team.name
                                }
                            }
                            toDelete[1].removeIf {
                                it as ParticipantStart
                                it.team == this@Team.name
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red, contentColor = Color.White)
                    ) { Text("Delete") }
                    Button(
                        onClick = { isVisible = !isVisible },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(red = 0, green = 200, blue = 0),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Add")
                    }
                }
            }

            AnimatedVisibility(isVisible) {
                Column {
                    var name by remember { mutableStateOf("") }
                    var surname by remember { mutableStateOf("") }
                    var yearOfBirth: Int? by remember { mutableStateOf(null) }
                    var isYearCorrect by remember { mutableStateOf(false) }
                    var isWishCorrect by remember { mutableStateOf(false) }
                    var isMale by remember { mutableStateOf(true) }
                    var rank by remember { mutableStateOf("") }
                    var wishGroupStr by remember { mutableStateOf("") }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Name")
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Surname")
                        OutlinedTextField(
                            value = surname,
                            onValueChange = {
                                surname = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rank")
                        OutlinedTextField(
                            value = rank,
                            onValueChange = {
                                rank = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Wish Group")
                        OutlinedTextField(
                            value = wishGroupStr,
                            onValueChange = {
                                wishGroupStr = it
                                isWishCorrect = toDelete[0].map { group ->
                                    group as Group
                                    group.groupName
                                }.contains(wishGroupStr)
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            isError = !isWishCorrect
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Year of birth")
                        OutlinedTextField(
                            value = yearOfBirth?.toString() ?: "",
                            onValueChange = { s ->
                                yearOfBirth = s.toIntOrNull().let {
                                    if (it != null) {
                                        isYearCorrect = true
                                        it
                                    } else {
                                        isYearCorrect = false
                                        null
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(0.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(5.dp),
                            isError = !isYearCorrect
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isMale = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (isMale) Color(
                                    red = 50,
                                    green = 50,
                                    blue = 255
                                ) else Color.Gray, contentColor = Color.White
                            )
                        ) { Text("Male") }
                        Button(
                            onClick = { isMale = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = if (!isMale) Color(
                                    red = 255,
                                    green = 28,
                                    blue = 89
                                ) else Color.Gray, contentColor = Color.White
                            )
                        ) { Text("Female") }
                    }
                    Button(onClick = {
                        val part =
                            Participant(if (isMale) Sex.MALE else Sex.FEMALE, surname, name, yearOfBirth ?: 0, rank)
                        part.setGroup(wishGroupStr)
                        listOfParticipant.add(part)
                        athleteList.add(part)
                        isVisible = false
                    }, enabled = isYearCorrect && isWishCorrect) { Text("Add part.") }
                }
            }
            var newList: List<Participant>
            AnimatedVisibility(isOpened) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Button(onClick = {
                            newList = listOfParticipant.sortedBy { it.name }
                            newList.forEachIndexed { index, participant ->
                                listOfParticipant[index] = participant
                            }
                        }, modifier = Modifier.weight(1f)) { Text("По имени ^") }
                        Button(
                            onClick = {
                                newList = listOfParticipant.sortedByDescending { it.name }
                                newList.forEachIndexed { index, participant ->
                                    listOfParticipant[index] = participant
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("По имени v") }
                        Button(onClick = {
                            newList = listOfParticipant.sortedBy { it.surname }
                            newList.forEachIndexed { index, participant ->
                                listOfParticipant[index] = participant
                            }
                        }, modifier = Modifier.weight(1f)) { Text("По фамилии ^") }
                        Button(
                            onClick = {
                                newList = listOfParticipant.sortedByDescending { it.surname }
                                newList.forEachIndexed { index, participant ->
                                    listOfParticipant[index] = participant
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("По фамилии v") }
                        Button(onClick = {
                            newList = listOfParticipant.sortedByDescending { it.yearOfBirth }
                            newList.forEachIndexed { index, participant ->
                                listOfParticipant[index] = participant
                            }
                        }, modifier = Modifier.weight(1f)) { Text("По возр. ^") }
                        Button(
                            onClick = {
                                newList = listOfParticipant.sortedBy { it.yearOfBirth }
                                newList.forEachIndexed { index, participant ->
                                    listOfParticipant[index] = participant
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("По возр. v") }
                    }
                    listOfParticipant.forEachIndexed { i, it ->
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(it.surname, modifier = Modifier.weight(1f))
                            Text(it.name, modifier = Modifier.weight(1f))
                            Text(if (it.sex == Sex.MALE) "M" else "Ж", modifier = Modifier.weight(1f))
                            Text(it.surname, modifier = Modifier.weight(1f))
                            Text(it.yearOfBirth.toString(), modifier = Modifier.weight(1f))
                            Text(it.rank.ifBlank { "-" }, modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    toDelete[0].forEach{
                                        it as Group
                                        it.listParticipants.removeIf { it.participant == listOfParticipant[i] }
                                    }
                                    toDelete[0].forEach { group ->
                                        group as Group
                                        group.listParticipants.removeIf {
                                            it.participant == listOfParticipant[i]
                                        }
                                    }
                                    toDelete[1].removeIf {
                                        it as ParticipantStart
                                        it.participant == listOfParticipant[i]
                                    }
                                    listOfParticipant.removeAt(i)
                                    athleteList.removeAt(i)
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
                }
            }
        }
    }
}