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

class Distance(name: String, type: DistanceTypeData) : Scrollable {
    private val pointsList: MutableList<ControlPoint> = mutableListOf()
    val name: String
    val type: DistanceTypeData

    init {
        emptyNameCheck(name, "Пустое имя дистанции")
        this.name = name
        this.type = type
    }

    fun getPointsList(): List<ControlPoint> {
        return pointsList.toMutableList()
    }

    fun addPoint(point: ControlPoint) {
        if (point.name.isBlank())
            throw UnexpectedValueException("Пустое имя контрольной точки")
        pointsList.add(point)
    }

    fun addAllPoints(points: Collection<ControlPoint>) {
        if (points.any { it.name.isBlank() })
            throw UnexpectedValueException("Пустое имя контрольной точки")
        pointsList.addAll(points)
    }

    fun checkProtocolPointsCorrectness(
        participant: ParticipantStart,
        participantDistance: Map<Int, List<ControlPointWithTime>>
    ): String {
        val start = ControlPointWithTime(ControlPoint("Start"), participant.startTime)
        var participantControlPoints = participantDistance[participant.number] ?: return "Снят"
        participantControlPoints = participantControlPoints.sortedBy { it.time.timeInSeconds }
        return when (type.type) {
            DistanceType.ALL_POINTS -> {
                if (participantControlPoints.map { it.point } != pointsList)
                    return "Снят"
                (participantControlPoints.maxByOrNull { it.time.timeInSeconds }!!.time - start.time).toString()
            }
            DistanceType.SOME_POINTS -> {
                var currentNumberOfPassedPoints = 0
                participantControlPoints.forEach {
                    var currentIndex = 0
                    while (currentIndex != pointsList.size && it.point != pointsList[currentIndex])
                        currentIndex++
                    if (currentIndex == pointsList.size)
                        return@forEach
                    if (pointsList[currentIndex] == it.point)
                        currentNumberOfPassedPoints++
                    if (currentNumberOfPassedPoints == type.numberOfPoints)
                        return (it.time - start.time).toString()
                }
                return "Снят"
            }
        }
    }

    fun createCSVHeader(numberOfPoints: Int): List<String> {
        val res = mutableListOf("Название", "Тип", "Количество точек", "Порядок")
        res.addAll(MutableList(numberOfPoints) { "$it" })
        return res
    }

    fun createCSVString(numberOfPoints: Int): List<String> {
        val res = mutableListOf(name, "${type.type}", "${type.numberOfPoints}", "${type.orderIsEssential}")
        res.addAll(pointsList.map { it.name })
        while (res.size < numberOfPoints)
            res.add("")
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
        val listOfParticipant = pointsList.toMutableStateList()

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
                    Text(this@Distance.name, modifier = Modifier.fillMaxWidth(0.8f).padding(start = 5.dp))
                    Button(onClick = { list.removeAt(index)
                        toDelete[0].removeIf { group ->
                            group as Group
                        if (group.distance==this@Distance) {
                            toDelete[1].removeIf {
                                it as ParticipantStart
                                it.wishGroup==group.groupName
                            }
                        }
                            this@Distance == group.distance
                        }
                    }) { Text("Delete") }
                }
            }

            AnimatedVisibility(isOpened) {
                Column {
                    listOfParticipant.forEachIndexed { i, it ->
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(it.name, modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    listOfParticipant.removeAt(i)
                                    pointsList.removeAt(i)
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


    override fun toString(): String {
        val s = StringBuilder("$name, Контрольные точки: $pointsList")
        return s.toString()
    }
}

