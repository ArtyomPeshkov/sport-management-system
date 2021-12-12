package ru.emkn.kotlin.sms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

val topRowHeight = 30.dp
val topButtonWidth = 100.dp
val separatorLineWidth = 1.dp

//@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    val buttonStates = remember { mutableStateOf(MutableList(10) { it == 0 }) }
    val path = remember { mutableStateOf("") }
    val phase = remember { mutableStateOf(-1) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test, TEST, AND ANOTHER TEST",
        state = rememberWindowState(width = 800.dp, height = 300.dp)
    ) {
        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.weight(4f)) { path.value = PathField() }
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) { DropDownMenu(phase) }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                              if (path.value.isNotBlank())
                              {
                                  when(path.value) {
                                      // Для этой фазы нужны: списки участников (папка applications),
                                      //                      информация о дистанциях (distances.csv),
                                      //                      информация о событии (event.csv),
                                      //                      информация о группах (groups.csv)
                                      "1" -> phase1(path.value) // После этой фазы будут сформированы стартовые протоколы в папке $path/starts
                                      // Для этой фазы нужны: стартовые протоколы (папка starts),
                                      //                      информация о дистанциях (distances.csv),
                                      //                      информация о группах (groups.csv)
                                      //                      информация о прохождении контрольных точек (points)
                                      "2" -> phase2(path.value) // После этой фазы будут сформированы протоколы прохождения дистанций в группах $path/results
                                      // Для этой фазы нужны: протоколы результатов (папка results),
                                      "3" -> phase3(path.value) // После этой фазы будут сформирован протокол результатов соревнований $path/teamsResults.csv
                                  }
                              }
                              /*TODO("Передает куда-то путь для проверки")*/
                              },
                    enabled = true
                ) { Text("Результат") }
            }
            AllTopButtons(1, buttonStates)
            Text(text = path.value)
            Text(phase.value.toString())
        }
    }
}

@Composable
fun PathField(): String {
    val textState = remember { mutableStateOf("") }
    OutlinedTextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        singleLine = true,
        shape = RoundedCornerShape(5.dp)
    )
    return textState.value
}

@Composable
fun DropDownMenu(phase: MutableState<Int>) {
    val expanded = remember { mutableStateOf(false) }
    val items = listOf("1", "2", "3")

    Text(
        if (phase.value != -1) items[phase.value-1] else "Phase",
        modifier = Modifier.border(3.dp, Color.Gray, RoundedCornerShape(6.dp))
            .clickable(onClick = { expanded.value = true })
            .padding(top = 20.dp, start = 5.dp, end = 5.dp, bottom = 20.dp).fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = if (phase.value != -1) Color.Black else Color.LightGray
    )
    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.padding(top = 5.dp).background(Color.White)
            .border(3.dp, Color.Gray, RoundedCornerShape(6.dp))
    ) {
        items.forEachIndexed { index, s ->
            DropdownMenuItem(onClick = {
                phase.value = index+1
                expanded.value = false
            }) {
                Text(s)
            }
        }
    }
}

@Composable
fun AllTopButtons(phase: Int, buttonStates: MutableState<MutableList<Boolean>>)/*: MutableList<Boolean>*/ {
    //TODO("Какие вкладки в какой фазу")
    val values =
        when (phase) {
            1 -> listOf("1", "2", "3", "4")
            2 -> listOf("4", "5", "6")
            else -> listOf("7", "8", "9")
        }
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