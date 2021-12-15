package ru.emkn.kotlin.sms

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

val topRowHeight = 30.dp
val topButtonWidth = 100.dp
val separatorLineWidth = 1.dp

fun listOfTabs(phase: Int): List<String> =
    when (phase) {
        1 -> listOf("Событие", "Команды", "Дистанции", "Группы", "Старт. прот.")
        2 -> listOf("Событие", "Старт. прот.", "Дистанции", "Контр. точки")
        3 -> listOf("Событие", "Результаты", "Общие")
        else -> emptyList()
    }


fun main() = application {
    val buttonStates = remember { mutableStateOf(MutableList(10) { it == 0 }) }
    var path by remember { mutableStateOf("") }
    val phase = remember { mutableStateOf(-1) }
    var test by remember { mutableStateOf(false) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test, TEST, AND ANOTHER TEST",
        state = rememberWindowState(width = 800.dp, height = 400.dp)
    ) {
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(4f)) { path = PathField() }
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) { DropDownMenu(phase, listOf(1, 2, 3)) }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        test = !test
                        /*TODO("Передает куда-то путь для проверки")*/
                        when (phase.value + 1) {
                            // Для этой фазы нужны: списки участников (папка applications),
                            //                      информация о дистанциях (distances.csv),
                            //                      информация о событии (event.csv),
                            //                      информация о группах (groups.csv)
                            1 -> phase1(path/*.value*/) // После этой фазы будут сформированы стартовые протоколы в папке $path/starts
                            // Для этой фазы нужны: стартовые протоколы (папка starts),
                            //                      информация о дистанциях (distances.csv),
                            //                      информация о группах (groups.csv)
                            //                      информация о прохождении контрольных точек (points)
                            2 -> phase2(path/*.value*/) // После этой фазы будут сформированы протоколы прохождения дистанций в группах $path/results
                            // Для этой фазы нужны: протоколы результатов (папка results),
                            3 -> phase3(path/*.value*/) // После этой фазы будут сформирован протокол результатов соревнований $path/teamsResults.csv
                        }
                    },
                    enabled = true
                ) {
                    Text("Результат")
                    Icon(
                        painter = painterResource("arrow.svg"),
                        contentDescription = null,
                        modifier = Modifier.width(18.dp).padding(start = 5.dp)
                    )
                }
            }
            AllTopButtons(2, buttonStates, listOfTabs(2))
            AnimatedVisibility(test) {
                Text(if (!test) path else "Scooby-doby-doooooooooooooooooo\nooooooooo\noooooooooooooo")
            }
            Text(phase.value.toString())
            val list = mutableStateListOf("Team 1", "Team 2", "Team 3", "Team 4")
            LazyScrollable(list)
        }
    }
}

@Composable
fun PathField(): String {
    var textState by remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState/*.value*/,
        onValueChange = { textState/*.value*/ = it },
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        singleLine = true,
        shape = RoundedCornerShape(5.dp)
    )
    return textState
}

@Composable
fun DropDownMenu(indexOfChoice: MutableState<Int>, items: List<Any>) {
    var expanded by remember { mutableStateOf(false) }
    Text(
        if (indexOfChoice.value >= 0) items[indexOfChoice.value].toString() else "Phase",
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
fun AllTopButtons(phase: Int, buttonStates: MutableState<MutableList<Boolean>>, values: List<String>) {
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
        modifier = Modifier.fillMaxHeight().fillMaxWidth()
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
fun Inner(str: String, Button: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
        Text(str)
        Button()
    }
}


@Composable
fun LazyScrollable(list: SnapshotStateList<String>) {
    Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val state = rememberLazyListState()

        LazyColumn(Modifier.fillMaxSize().padding(end = 12.dp), state) {
            items(list) {
                Inner(it) { Button(onClick = { list.remove(it) }, content = { }) }
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