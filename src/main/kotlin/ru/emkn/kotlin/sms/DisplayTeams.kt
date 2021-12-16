package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

val topRowHeight = 30.dp
val separatorLineWidth = 1.dp

fun listOfTabs(phase: Int): List<String> =
    when (phase) {
        1 -> listOf("Событие", "Команды", "Дистанции", "Группы", "Старт. прот.")
        2 -> listOf("Событие", "Старт. прот.", "Дистанции", "Контр. точки")
        3 -> listOf("Событие", "Результаты", "Общие")
        else -> emptyList()
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
            Box(modifier = Modifier.weight(4f)) { path.value = PathField() }
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
fun PhaseOneWindow() {
    val currentPhase = 1
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    val list = mutableStateListOf("Team 1", "Team 2", "Team 3", "Team 4")
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
        LazyScrollable(list)
    }
}

@Composable
fun PhaseTwoWindow() {
    val currentPhase = 2
    val buttonStates = remember { mutableStateOf(MutableList(listOfTabs(currentPhase).size) { it == 0 }) }
    Column {
        AllTopButtons(buttonStates, listOfTabs(currentPhase))
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
    val path = remember { mutableStateOf("") }

    Window(
        onCloseRequest = ::exitApplication,
        title = if (phase.value == -1) "Application" else "Phase ${phase.value + 1}",
        state = rememberWindowState(width = if (phase.value == -1) 600.dp else 800.dp, height = 400.dp)
    ) {
        when (phase.value) {
            -1 -> PhaseChoice(path, phase)
            0 -> PhaseOneWindow()
            1 -> PhaseTwoWindow()
            2 -> PhaseThreeWindow()
        }
    }
}

fun window() = application {
    val buttonStates = remember { mutableStateOf(MutableList(10) { it == 0 }) }
    var path by remember { mutableStateOf("") }
    val phase = remember { mutableStateOf(-1) }
    var test by remember { mutableStateOf(false) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test, TEST, AND ANOTHER TEST",
        state = rememberWindowState(width = 800.dp, height = 400.dp)
    ) {
        val list = mutableStateListOf("Team 1", "Team 2", "Team 3", "Team 4")
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(4f)) { path = PathField() }
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) { DropDownMenu(phase, listOf(1, 2, 3), "Phase") }
                val isRotated = remember { mutableStateOf(false) }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        test = !test
                        isRotated.value = !isRotated.value
                        /*TODO("Передает куда-то путь для проверки")*/
                        when (phase.value + 1) {
                            // Для этой фазы нужны: списки участников (папка applications),
                            //                      информация о дистанциях (distances.csv),
                            //                      информация о событии (event.csv),
                            //                      информация о группах (groups.csv)
                            1 -> phase1(path) // После этой фазы будут сформированы стартовые протоколы в папке $path/starts
                            // Для этой фазы нужны: стартовые протоколы (папка starts),
                            //                      информация о дистанциях (distances.csv),
                            //                      информация о группах (groups.csv)
                            //                      информация о прохождении контрольных точек (points)
                            2 -> phase2(path) // После этой фазы будут сформированы протоколы прохождения дистанций в группах $path/results
                            // Для этой фазы нужны: протоколы результатов (папка results),
                            3 -> phase3(path) // После этой фазы будут сформирован протокол результатов соревнований $path/teamsResults.csv
                        }
                    },
                    enabled = true
                ) {
                    val angle: Float by animateFloatAsState(
                        targetValue = if (isRotated.value) 90F else 0F,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                    Icon(
                        painter = painterResource("arrow.svg"),
                        contentDescription = null,
                        modifier = Modifier.width(10.dp).rotate(angle)
                    )
                    Text(text = "Результат", modifier = Modifier.padding(start = 10.dp), fontSize = 12.sp)
                }
            }
            AllTopButtons(buttonStates, listOfTabs(2))
            AnimatedVisibility(test) {
                Text(if (!test) path else "Scooby-doby-doooooooooooooooooo\nooooooooo\noooooooooooooo")
            }
            Text(phase.value.toString())
            LazyScrollable(list)
        }
    }
}

@Composable
fun PathField(): String {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState,
        onValueChange = { textState = it },
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        singleLine = true,
        shape = RoundedCornerShape(5.dp)
    )
    return textState
}

@Composable
fun DropDownMenu(indexOfChoice: MutableState<Int>, items: List<Any>, text: String) {
    var expanded by remember { mutableStateOf(false) }
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
           /* .padding(start = 5.dp, end = 5.dp)*/,
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
fun <T> LazyItem(str: T, Button: @Composable () -> Unit) {
    str as String
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        Text(str)
        Button()
    }
}


@Composable
fun <T> LazyScrollable(list: SnapshotStateList<T>) {
    Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val state = rememberLazyListState()

        LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
            items(list) {
                LazyItem(it) { Button(onClick = { list.remove(it) }) { Text("Hi-Hi-Hi") } }
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

fun dialog() = application {
    //Первый вид окон (для него нужны функции снизу)
    Dialog(onCloseRequest = ::exitApplication) {
        ComposeDialogDemo()
    }

    //Второй вид окон
    val mode = remember { mutableStateOf(0) }
    when (mode.value) {
        0 -> Dialog(onCloseRequest = ::exitApplication) {
            Button(
                onClick = { mode.value = 1 },
                content = { Text("A") })
        }
        1 -> Dialog(onCloseRequest = ::exitApplication) {
            Button(
                onClick = { mode.value = 0 },
                content = { Text("B") })
        }
        else -> throw Exception()
    }
}

@Composable
fun ComposeDialogDemo() {
    // State to manage if the alert dialog is showing or not.
    // Default is false (not showing)
    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    Column(
        // Make the column fill the whole screen space (width and height).
        modifier = Modifier.fillMaxSize(),
        // Place all children at center horizontally.
        horizontalAlignment = Alignment.CenterHorizontally,
        // Place all children at center vertically.
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                setShowDialog(true)
            }) {
            Text("Show Dialog")
        }
        // Create alert dialog, pass the showDialog state to this Composable
        DialogDemo(showDialog, setShowDialog)
    }
}

@Composable
fun DialogDemo(showDialog: Boolean, setShowDialog: (Boolean) -> Unit) {
    if (showDialog) {
        Dialog(
            onCloseRequest = { setShowDialog(false) },
            title = "Title",
        ) {
            Text("Any")
        }
    }
}


// Нормальные окна

//fun main() = application {
//    var content by remember { mutableStateOf(true) }
//
//    if (content)
//        Window(
//            onCloseRequest = ::exitApplication,
//            title = "Window 111",
//            state = rememberWindowState(width = 200.dp, height = 100.dp)
//        ) {
//            Button(onClick = { content = !content }) { Text("Window 1") }
//        }
//    else
//        Window(
//            onCloseRequest = ::exitApplication,
//            title = "Window 222",
//            state = rememberWindowState(width = 800.dp, height = 400.dp)
//        ) {
//            Button(onClick = { content = !content }) { Text("Window 2") }
//        }
//}