package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File

val topRowHeight = 30.dp
val separatorLineWidth = 1.dp

fun listOfTabs(phase: Int): List<String> =
    when (phase) {
        1 -> listOf("Команды", "Дистанции", "Группы", "Старт. прот.")
        2 -> listOf("Старт. прот.", "Дистанции")
        3 -> listOf("Результаты", "Общие")
        else -> emptyList()
    }

@Composable
fun eventDataOnScreen(eventData: MutableState<Event>) {
    Column {
        Text(eventData.value.name)
        Text(eventData.value.date.toString())
    }
}

@Composable
fun teamsDataOnScreen(teamList: SnapshotStateList<Team>) {
    Column {
        LazyScrollable(teamList)
        Button(onClick = {
            File("test.csv").createNewFile(); csvWriter().writeAll(
            listOf(teamList),
            File("test.csv")
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun distancesDataOnScreen(distanceList: SnapshotStateList<Distance>) {
    Column {
        LazyScrollable(distanceList)
        Button(onClick = {
            File("test.csv").createNewFile(); csvWriter().writeAll(
            listOf(distanceList),
            File("test.csv")
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun groupsDataOnScreen(groupList: SnapshotStateList<Group>) {
    Column {
        LazyScrollable(groupList)
        Button(onClick = {
            File("test.csv").createNewFile(); csvWriter().writeAll(
            listOf(groupList),
            File("test.csv")
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun startProtocolsDataOnScreen(
    participantList: SnapshotStateList<ParticipantStart>
) {
    Column {
        LazyScrollable(participantList)
        Button(onClick = {
            File("test.csv").createNewFile(); csvWriter().writeAll(
            listOf(participantList),
            File("test.csv")
        )
        }) {
            Text("Save")
        }
    }
}

@Composable
fun passingOfControlPointsDataOnScreen() {

}

@Composable
fun resultsOnScreen() {

}

@Composable
fun PhaseChoice(path: MutableState<String>, phase: MutableState<Int>) {
    val phaseFromDropDown = remember { mutableStateOf(-1) }
    Column(modifier = Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(modifier = Modifier.weight(4f)) { PathField(path) }
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                DropDownMenu(
                    phaseFromDropDown,
                    listOf(1, 2, 3),
                    "Phase"
                )
            }
        }
        Button(
            onClick = {/*TODO(отправить куда-то для проверки)*/ phase.value = phaseFromDropDown.value },
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
    eventData: MutableState<Event>
) {
    val currentPhase = 1
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
        eventDataOnScreen(eventData)
        when (buttonStates.value.indexOf(true)) {
            0 -> teamsDataOnScreen(teamList)
            1 -> distancesDataOnScreen(distanceList)
            2 -> groupsDataOnScreen(groupList)
            3 -> {
                val allParticipants = groupList.flatMap { it.listParticipants }.toMutableStateList()
                startProtocolsDataOnScreen(allParticipants)
            }
        }
    }
}

@Composable
fun PhaseTwoWindow(
    distanceList: SnapshotStateList<Distance>,
    eventData: MutableState<Event>,
    participantList: SnapshotStateList<ParticipantStart>
) {
    val currentPhase = 2
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
        eventDataOnScreen(eventData)
        when (buttonStates.value.indexOf(true)) {
            0 -> startProtocolsDataOnScreen(participantList)
            1 -> distancesDataOnScreen(distanceList)
        }
    }
}

@Composable
fun PhaseThreeWindow() {
    val currentPhase = 3
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
    }
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
            val groups = GroupReader(configFolder).getGroups(distances, Phase.FIRST)
            val teams = TeamReader(configFolder).getTeams()

            val (name, date) = getNameAndDate(readFile(configFolder).walk().toList(), configFolder)
            val event = Event(name, date, groups, distances, teams)
            event.getDistanceList().forEach {
                event.setNumbersAndTime(event.getGroupsByDistance(it.value))
            }
            event.makeStartProtocols()
            generateCP(controlPoints, groups)

            Window(
                onCloseRequest = ::exitApplication,
                title = "Phase ${phase.value + 1}",
                state = rememberWindowState(width = if (phase.value == -1) 600.dp else 800.dp, height = 400.dp)
            ) {
                val distanceList = remember { distances.values.toMutableStateList() }
                val groupList = remember { groups.toMutableStateList() }
                val teamList = remember { teams.toMutableStateList() }
                val eventData = remember { mutableStateOf(event) }

                PhaseOneWindow(distanceList, groupList, teamList, eventData)

                println(teamList.size)
            }

        }
        1 -> {
            val configurationFolder = path.value

            val distances = DistanceReader(configurationFolder).getDistances()

            val groups = GroupReader(configurationFolder).getGroups(distances, Phase.SECOND)
            StartProtocolParse(configurationFolder).getStartProtocolFolder(groups)
            val (name, date) = getNameAndDate(readFile(configurationFolder).walk().toList(), configurationFolder)
            val event = Event(name, date, groups, distances)
            val participantDistanceWithTime: Map<Int, List<ControlPointWithTime>> =
                ControlPointReader(configurationFolder).getPoints()
            setStatusForAllParticipants(groups, distances, participantDistanceWithTime)

            makeResultProtocols(groups)
            Window(
                onCloseRequest = ::exitApplication,
                title = "Phase ${phase.value + 1}",
                state = rememberWindowState(width = if (phase.value == -1) 600.dp else 800.dp, height = 400.dp)
            ) {
                val distanceList = remember { distances.values.toMutableStateList() }
                val participantList = remember { groups.flatMap { it.listParticipants }.toMutableStateList() }
                val eventData = remember { mutableStateOf(event) }

                PhaseTwoWindow(distanceList, eventData, participantList)

                println(participantList.size)
            }
        }
        2 -> PhaseThreeWindow()
    }
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
fun DropDownMenu(indexOfChoice: MutableState<Int>, items: List<Any>, text: String) {
    var expanded by remember { mutableStateOf(false) }
    //Column
    Text(
        if (indexOfChoice.value >= 0) items[indexOfChoice.value].toString() else text,
        modifier = Modifier.border(3.dp, Color.Gray, RoundedCornerShape(6.dp))
            .clickable(onClick = { expanded/*.value*/ = true })
            .padding(top = 20.dp, start = 5.dp, end = 5.dp, bottom = 20.dp).fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = if (indexOfChoice.value >= 0) Color.Black else Color.LightGray
    )
    DropdownMenu(
        expanded = expanded/*.value*/,
        onDismissRequest = { expanded/*.value*/ = false },
        modifier = Modifier.padding(top = 5.dp).background(Color.White)
            .border(3.dp, Color.Gray, RoundedCornerShape(6.dp))
    ) {
        items.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                indexOfChoice.value = index
                expanded/*.value*/ = false
            }) {
                Text(s.toString())
            }
        }
    }
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
fun <T : Scrollable> LazyScrollable(list: SnapshotStateList<T>) {

    Box(modifier = Modifier.fillMaxWidth().padding(10.dp).fillMaxHeight(.75f)) {
        val state = rememberLazyListState()

        LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
            itemsIndexed(list) { index, it ->
                it.show(list, index)
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
