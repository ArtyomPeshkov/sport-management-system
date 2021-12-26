package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import exceptions.UnexpectedValueException
import java.io.File
import java.time.LocalDate

val topRowHeight = 30.dp
val separatorLineWidth = 1.dp

@Composable
fun eventDataOnScreen(
    eventData: MutableState<Event>,
    isDateCorrect: MutableState<Boolean>,
    dateString: MutableState<String>
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Event name: ")
            OutlinedTextField(
                value = eventData.value.name,
                onValueChange = {
                    eventData.value.name = it
                },
                modifier = Modifier.fillMaxWidth().padding(0.dp),
                singleLine = true,
                shape = RoundedCornerShape(5.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Event data: ")
            OutlinedTextField(
                value = dateString.value,
                onValueChange = {
                    //Сохранение
                    dateString.value = it
                    if (Regex("""[0-9]{4}-[0-9]{2}-[0-9]{2}""").matches(dateString.value) &&
                        dateString.value.substringAfter('-').substringBefore('-').toInt() in 1..12 &&
                        dateString.value.substringAfterLast('-').toInt() in 1..28

                    ) {
                        eventData.value.date = LocalDate.parse(dateString.value)
                        isDateCorrect.value = true
                    } else
                        isDateCorrect.value = false
                },
                modifier = Modifier.fillMaxWidth().padding(0.dp),
                singleLine = true,
                shape = RoundedCornerShape(5.dp),
                isError = !isDateCorrect.value
            )
        }

        Text(eventData.value.name)
        Text(eventData.value.date.toString())
    }
}

@Composable
fun teamsDataOnScreen(
    teamList: SnapshotStateList<Team>,
    configurationFolder: String,
    groupList: SnapshotStateList<Group>,
    participantList: SnapshotStateList<ParticipantStart>
) {
    var isVisible by remember { mutableStateOf(false) }
    var newList: List<Team>
    Column {
        Row(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(onClick = {
                newList = teamList.sortedBy { it.name }
                newList.forEachIndexed { index, team ->
                    teamList[index] = team
                }
            }, modifier = Modifier.weight(1f)) { Text("По имени ^") }
            Button(
                onClick = {
                    newList = teamList.sortedByDescending { it.name }
                    newList.forEachIndexed { index, team ->
                        teamList[index] = team
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По имени v") }
            Button(onClick = {
                newList = teamList.sortedBy { it.athleteList.size }
                newList.forEachIndexed { index, team ->
                    teamList[index] = team
                }
            }, modifier = Modifier.weight(1f)) { Text("По кол-ву ^") }
            Button(
                onClick = {
                    newList = teamList.sortedByDescending { it.athleteList.size }
                    newList.forEachIndexed { index, team ->
                        teamList[index] = team
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По кол-ву v") }
        }
        LazyScrollable(teamList, false, listOf(groupList, participantList))
        Button(onClick = {
            // НЕ РАБОТАЕТ
            if (File("$configurationFolder/save/application").exists())
                File("$configurationFolder/save/application").deleteRecursively()
            File("$configurationFolder/save/application").mkdirs()
            File("$configurationFolder/save/application").createNewFile()
            teamList.forEach {
                val teams = it.createCSVHeader()
                teams.addAll(it.createCSVStrings())
                csvWriter().writeAll(
                    teams,
                    File("$configurationFolder/save/application/application_${it.name}.csv"),
                    append = false
                )
            }
        }) {
            Text("Save")
        }
        Button(
            onClick = {
                isVisible = !isVisible
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(red = 0, green = 200, blue = 0),
                contentColor = Color.White
            )
        ) {
            Text("Add team")
        }


        AnimatedVisibility(isVisible) {
            Column {
                var groupName by remember { mutableStateOf("") }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Team Name")
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = {
                            groupName = it
                        },
                        modifier = Modifier.fillMaxWidth().padding(0.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp)
                    )
                }
                Button(onClick = {
                    teamList.add(Team(groupName))
                    isVisible = false
                }) { Text("Add team") }
            }
        }
    }
}


@Composable
fun distancesDataOnScreen(
    distanceList: SnapshotStateList<Distance>,
    configurationFolder: String,
    groupList: SnapshotStateList<Group>,
    participantList: SnapshotStateList<ParticipantStart>
) {
    var isVisible by remember { mutableStateOf(false) }
    var newList: List<Distance>
    Column {
        Row(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(onClick = {
                newList = distanceList.sortedBy { it.name }
                newList.forEachIndexed { index, distance ->
                    distanceList[index] = distance
                }
            }, modifier = Modifier.weight(1f)) { Text("По имени ^") }
            Button(
                onClick = {
                    newList = distanceList.sortedByDescending { it.name }
                    newList.forEachIndexed { index, distance ->
                        distanceList[index] = distance
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По имени v") }
            Button(onClick = {
                newList = distanceList.sortedBy { it.getPointsList().size }
                newList.forEachIndexed { index, distance ->
                    distanceList[index] = distance
                }
            }, modifier = Modifier.weight(1f)) { Text("По кол-ву ^") }
            Button(
                onClick = {
                    newList = distanceList.sortedByDescending { it.getPointsList().size }
                    newList.forEachIndexed { index, distance ->
                        distanceList[index] = distance
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По кол-ву v") }
        }
        LazyScrollable(distanceList, true, listOf(groupList, participantList))
        Button(onClick = {
            //Создать функции для сохранения различных видов структур и обработать возможные ошибки (Например: удалили дистанцию D4000, значит с группой, которая отвечает за эту дистанцию надо что-то сделать)
            // Возможное решение: находить все конфликты после попытки сохранить новые данные и если удалённая структура где-то использовалась предложить пользователю поменять эту структуру в этом месте или отказаться от изменений
            // Создать для каждой фазы отдельный класс, хранящий все данные фазы и через него проверять все конфликты после удаления
            // (Функция будет принимать класс, реализующий интерфейс Phase, внутри которого будет функция checkConflicts, а Phase будет реализован в Phase1 Phase2 Phase3)
            // Например функция distancesDataOnScreen может принимать как Phase1, так и Phase2, но конфликты в них могут возникнуть разные (или одинаковые, хрен его знает, 4 часа ночи. Я не буду думать, что там разное, а что одинаковое)
            // Выпадающий список с доступными дистанциями
            // Создать папку saves внутри папки переданной пользователем и проверить корректность работы программы. Если что-то работает не так, попросить пользователя внести необходимые правки.
            File("$configurationFolder/save/").mkdirs()
            File("$configurationFolder/save/distances.csv").createNewFile()

            val maxNumberOfPoints = try {
                distanceList.maxOf { it.getPointsList().size }
            } catch (e: NoSuchElementException) {
                0
            }
            val distances = try {
                mutableListOf(distanceList[0].createCSVHeader(maxNumberOfPoints))
            } catch (e: IndexOutOfBoundsException) {
                mutableListOf(
                    Distance("Any", DistanceTypeData(DistanceType.ALL_POINTS, 100, true)).createCSVHeader(
                        maxNumberOfPoints
                    )
                )
            }
            distanceList.forEach {
                distances.add(it.createCSVString(maxNumberOfPoints))
            }
            csvWriter().writeAll(
                distances,
                File("$configurationFolder/save/distances.csv"),
                append = false
            )
        }) {
            Text("Save")
        }
        Button(
            onClick = { isVisible = !isVisible },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(red = 0, green = 200, blue = 0),
                contentColor = Color.White
            )
        ) { Text("Add distance") }

        AnimatedVisibility(isVisible) {
            Column {
                var distanceName by remember { mutableStateOf("") }
                var type by remember { mutableStateOf(true) }
                var numberOfPoints: Int? by remember { mutableStateOf(null) }
                var isNameCorrect by remember { mutableStateOf(false) }
                var isNumberCorrect by remember { mutableStateOf(true) }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Distance Name")
                    OutlinedTextField(
                        value = distanceName,
                        onValueChange = { str ->
                            distanceName = str
                            isNameCorrect = !distanceList.map { it.name }.contains(distanceName) && str.isNotBlank()
                        },
                        modifier = Modifier.fillMaxWidth().padding(0.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        isError = !isNameCorrect
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Number of need points\nIf all points, ignore it")
                    OutlinedTextField(
                        value = numberOfPoints?.toString() ?: "",
                        onValueChange = { str ->
                            numberOfPoints = str.toIntOrNull()
                            isNameCorrect = !distanceList.map { it.name }.contains(distanceName) && str.isNotBlank()
                            isNumberCorrect = if (type) true else numberOfPoints != null && numberOfPoints != 0
                        },
                        modifier = Modifier.fillMaxWidth().padding(0.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp),
                        isError = !isNameCorrect || !isNumberCorrect
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { type = true },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (type) Color.Blue else Color.Gray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) { Text("All points") }
                    Button(
                        onClick = { type = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (!type) Color.Blue else Color.Gray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) { Text("Some points") }
                }
                Button(
                    onClick = {
                        val dist = Distance(
                            distanceName,
                            DistanceTypeData(
                                if (type) DistanceType.ALL_POINTS else DistanceType.SOME_POINTS,
                                if (type) 1 else numberOfPoints ?: 1,
                                true
                            )
                        )
                        if (type) {
                            dist.addPoint(ControlPoint("100"))
                        } else
                            for (i in 1..(numberOfPoints ?: 0))
                                dist.addPoint(ControlPoint(i.toString()))
                        distanceList.add(dist)
                        isVisible = false
                    },
                    enabled = isNameCorrect && isNumberCorrect
                ) { Text("Add distance") }
            }
        }
    }
}


@Composable
fun groupsDataOnScreen(
    groupList: SnapshotStateList<Group>,
    configurationFolder: String,
    participantList: SnapshotStateList<ParticipantStart>
) {
    var newList: List<Group>
    Column {
        Row(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(onClick = {
                newList = groupList.sortedBy { it.groupName }
                newList.forEachIndexed { index, group ->
                    groupList[index] = group
                }
            }, modifier = Modifier.weight(1f)) { Text("По имени ^") }
            Button(
                onClick = {
                    newList = groupList.sortedByDescending { it.groupName }
                    newList.forEachIndexed { index, group ->
                        groupList[index] = group
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По имени v") }
            Button(onClick = {
                newList = groupList.sortedBy { it.ageFrom }
                newList.forEachIndexed { index, group ->
                    groupList[index] = group
                }
            }, modifier = Modifier.weight(1f)) { Text("По возрасту ^") }
            Button(
                onClick = {
                    newList = groupList.sortedByDescending { it.ageTo }
                    newList.forEachIndexed { index, group ->
                        groupList[index] = group
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По возрасту v") }
        }
        LazyScrollable(groupList, true, listOf(participantList))
        Button(onClick = {
            File("$configurationFolder/save/").mkdirs()
            File("$configurationFolder/save/distances.csv").createNewFile()

            val groups = try {
                mutableListOf(groupList[0].createCSVHeader())
            } catch (e: IndexOutOfBoundsException) {
                mutableListOf(
                    Group(
                        "Any",
                        Distance("Any", DistanceTypeData(DistanceType.ALL_POINTS, 100, true))
                    ).createCSVHeader()
                )
            }
            groupList.forEach {
                groups.add(it.createCSVString())
            }
            csvWriter().writeAll(
                groups,
                File("$configurationFolder/save/groups.csv"),
                append = false
            )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun startProtocolsDataOnScreen(
    participantList: SnapshotStateList<ParticipantStart>,
    isDeletable: Boolean = true,
    groupList: SnapshotStateList<Group>,
    event: MutableState<Event>,
    configurationFolder: String
) {
    var newList: List<ParticipantStart>
    Column {
        Row(
            modifier = Modifier.padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Button(onClick = {
                newList = participantList.sortedBy { it.name }
                newList.forEachIndexed { index, participantStart ->
                    participantList[index] = participantStart
                }
            }, modifier = Modifier.weight(1f)) { Text("По имени ^") }
            Button(
                onClick = {
                    newList = participantList.sortedByDescending { it.name }
                    newList.forEachIndexed { index, participantStart ->
                        participantList[index] = participantStart
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По имени v") }
            Button(onClick = {
                newList = participantList.sortedBy { it.surname }
                newList.forEachIndexed { index, participantStart ->
                    participantList[index] = participantStart
                }
            }, modifier = Modifier.weight(1f)) { Text("По фамилии ^") }
            Button(
                onClick = {
                    newList = participantList.sortedByDescending { it.surname }
                    newList.forEachIndexed { index, participantStart ->
                        participantList[index] = participantStart
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По фамилии v") }
            Button(onClick = {
                newList = participantList.sortedBy { it.number }
                newList.forEachIndexed { index, participantStart ->
                    participantList[index] = participantStart
                }
            }, modifier = Modifier.weight(1f)) { Text("По номеру ^") }
            Button(
                onClick = {
                    newList = participantList.sortedByDescending { it.number }
                    newList.forEachIndexed { index, group ->
                        participantList[index] = group
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По номеру v") }
            Button(onClick = {
                newList = participantList.sortedByDescending { it.yearOfBirth }
                newList.forEachIndexed { index, participantStart ->
                    participantList[index] = participantStart
                }
            }, modifier = Modifier.weight(1f)) { Text("По возр. ^") }
            Button(
                onClick = {
                    newList = participantList.sortedBy { it.yearOfBirth }
                    newList.forEachIndexed { index, participantStart ->
                        participantList[index] = participantStart
                    }
                },
                modifier = Modifier.weight(1f)
            ) { Text("По возр. v") }
        }
        LazyScrollable(
            participantList.map { ParticipantStartProtocol(it) }.toMutableStateList(),
            isDeletable,
            listOf(groupList)
        )
        if (isDeletable)
            Button(onClick = {
                File("test.csv").createNewFile(); csvWriter().writeAll(
                listOf(participantList),
                File("test.csv")
            )
            }) {
                Text("Save")
            }
        Button(onClick = {
            event.value.getDistanceList().forEach {
                event.value.setNumbersAndTime(event.value.getGroupsByDistance(it.value))
            }
            event.value.makeStartProtocols("$configurationFolder/save/")
        }) {
            Text("Refresh")
        }
    }
}

@Composable
fun resultsOnScreen() {

}

@Composable
fun PhaseChoice(path: MutableState<String>, phase: MutableState<Int>) {
    Column(modifier = Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Path to config folder: ")
            Box(modifier = Modifier.fillMaxWidth()) { PathField(path) }
        }
        Button(
            onClick = { phase.value = 0 },
            modifier = Modifier.fillMaxWidth()
        )
        { Text(text = "Результаты") }
    }
}

@Composable
fun PhaseOneWindow(
    distanceList: SnapshotStateList<Distance>,
    groupList: SnapshotStateList<Group>,
    teamList: SnapshotStateList<Team>,
    eventData: MutableState<Event>,
    configurationFolder: String,
    participantList: SnapshotStateList<ParticipantStart>,
    controlPoints: SnapshotStateList<ControlPoint>,
    listWithCP: SnapshotStateMap<ParticipantStart, List<ControlPointWithTime>>,
) {
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs.size) { it == 0 }) }
    val isDateCorrect = remember { mutableStateOf(true) }
    val dateString = remember { mutableStateOf("") }
    dateString.value = eventData.value.date.toString()
    var pathToCP by remember { mutableStateOf("") }
    Column {
        AllTopButtons(buttonStates, listOfTabs)
        eventDataOnScreen(eventData, isDateCorrect, dateString)
        when (buttonStates.value.indexOf(true)) {
            0 -> teamsDataOnScreen(teamList, configurationFolder, groupList, participantList)
            1 -> distancesDataOnScreen(distanceList, configurationFolder, groupList, participantList)
            2 -> groupsDataOnScreen(groupList, configurationFolder, participantList)
            3 -> {
                startProtocolsDataOnScreen(
                    participantList,
                    false,
                    groupList,
                    mutableStateOf(
                        Event(
                            eventData.value.name,
                            eventData.value.date,
                            groupList,
                            distanceList.associateBy { it.name }
                        )
                    ),
                    configurationFolder
                )
            }
            4 -> {
                Column {
                    controlPointsDataOnScreen(listWithCP)
                    Button(onClick = {
                        listWithCP.clear()
                        val allParticipants = groupList.flatMap { it.listParticipants }
                        listWithCP.putAll(
                            ControlPointReader(configurationFolder).getPoints()
                                .mapKeys { entry ->
                                    allParticipants.find { it.number == entry.key }
                                        ?: throw UnexpectedValueException("")
                                })
                        generateCP(controlPoints, groupList, configurationFolder)
                    }) { Text("Random results generator") }
                    OutlinedTextField(
                        value = pathToCP,
                        onValueChange = {
                            pathToCP = it
                        },
                        modifier = Modifier.fillMaxWidth().padding(0.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(5.dp)
                    )
                    Button(onClick = {
                        val controlPointsMap = ControlPointReader(pathToCP).getPoints()
                        listWithCP.clear()
                        listWithCP.putAll(controlPointsMap.filter { mapInput -> participantList.find { it.number == mapInput.key } != null }
                            .mapKeys { mapInput ->
                                participantList.find { it.number == mapInput.key }
                                    ?: throw UnexpectedValueException("No such participant ${mapInput.key}")
                            })
                    }) { Text("Read results") }
                }
            }
            5 -> {
            //    groupResultsDataOnScreen()
                Button(onClick = {
                    val allParticipants = groupList.flatMap { it.listParticipants }
                    listWithCP.putAll(
                        ControlPointReader(configurationFolder).getPoints()
                            .mapKeys { entry ->
                                allParticipants.find { it.number == entry.key }
                                    ?: throw UnexpectedValueException("")
                            })
                    generateCP(controlPoints, groupList, configurationFolder)
                }) { Text("Generate results for groups") }
            }
        }
    }
}

@Composable
fun groupResultsDataOnScreen(participantResultList: SnapshotStateMap<Group, List<ParticipantResult>>) {
    LazyScrollable(
        participantResultList.keys.toMutableStateList(),
        false,
        listOf(participantResultList.values.toMutableStateList())
    )
}

@Composable
fun controlPointsDataOnScreen(participantList: SnapshotStateMap<ParticipantStart, List<ControlPointWithTime>>) {
    LazyScrollable(
        participantList.keys.toMutableStateList(),
        false,
        listOf(participantList.values.toMutableStateList())
    )
}

fun main() = application {
    val phase = remember { mutableStateOf(-1) }
    val path = remember { mutableStateOf("csvFiles/configuration") }

    when (phase.value) {
        -1 -> {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Application",
                state = rememberWindowState(width = 600.dp, height = 400.dp)
            ) {
                PhaseChoice(path, phase)
            }
        }
        0 -> {
            val configFolder = path.value
            val controlPoints = mutableListOf<ControlPoint>()

            val distances = DistanceReader(configFolder).getDistances(controlPoints)
            val groups = GroupReader(configFolder).getGroups(distances)
            val teams = TeamReader(configFolder).getTeams()

            val (name, date) = getNameAndDate(readFile(configFolder).walk().toList(), configFolder)
            val event = Event(name, date, groups, distances, teams)
            event.getDistanceList().forEach {
                event.setNumbersAndTime(event.getGroupsByDistance(it.value))
            }
            event.makeStartProtocols(configFolder)
            //Кнопка для генерации
            generateCP(controlPoints, groups, configFolder)

            Window(
                onCloseRequest = ::exitApplication,
                title = "Phase ${phase.value + 1}",
                state = rememberWindowState(width = if (phase.value == -1) 600.dp else 850.dp, height = 400.dp)
            ) {
                val distanceList = remember { distances.values.toMutableStateList() }
                val groupList = remember { groups.toMutableStateList() }
                val teamList = remember { teams.toMutableStateList() }
                val participantList = remember { groupList.flatMap { it.listParticipants }.toMutableStateList() }
                val eventData = remember { mutableStateOf(event) }
                val listWithCP = mutableStateMapOf<ParticipantStart, List<ControlPointWithTime>>()

                PhaseOneWindow(
                    distanceList,
                    groupList,
                    teamList,
                    eventData,
                    configFolder,
                    participantList,
                    controlPoints.toMutableStateList(),
                    listWithCP
                )
            }
        }
    }
}

@Composable
fun GroupResultsOnScreen(groupList: SnapshotStateList<Group>) {

}


@Composable
fun PathField(path: MutableState<String>) {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            path.value = it
        },
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        singleLine = true,
        shape = RoundedCornerShape(5.dp)
    )
}

@Composable
fun AllTopButtons(buttonStates: MutableState<MutableList<Boolean>>, values: List<String>) {
    Row(
        modifier = Modifier.height(topRowHeight).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        values.forEachIndexed { index, s ->
            if (index != values.lastIndex) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    TopButton(
                        s,
                        index,
                        buttonStates
                    )
                }
                SeparatorLine()
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            TopButton(
                values.last(),
                values.lastIndex,
                buttonStates
            )
        }
    }
}

@Composable
fun TopButton(text: String, index: Int, buttonStates: MutableState<MutableList<Boolean>>) {
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                buttonStates.value = MutableList(buttonStates.value.size) { it == index }
            }.background(color = if (buttonStates.value[index]) Color.White else Color.LightGray)
            .padding(start = 5.dp, end = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, textAlign = TextAlign.Center, color = Color.Black)
    }
}

@Composable
fun SeparatorLine() {
    Box(modifier = Modifier.fillMaxHeight().width(separatorLineWidth).background(Color.Black)) {}
}

@Composable
fun <T : Scrollable, E : Any> LazyScrollable(
    list: SnapshotStateList<T>,
    isDeletable: Boolean = true,
    toDelete: List<SnapshotStateList<out E>>
) {

    Box(modifier = Modifier.fillMaxWidth().padding(10.dp).fillMaxHeight(.5f)) {
        val state = rememberLazyListState()

        LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
            itemsIndexed(list) { index, it ->
                it.show(list, index, isDeletable, toDelete)
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = state
            )
        )
    }
}
