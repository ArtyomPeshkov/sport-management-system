package ru.emkn.kotlin.sms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
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

@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    val buttonStates = remember { mutableStateOf(MutableList(10) { it == 0 }) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test, TEST, AND ANOTHER TEST",
        state = rememberWindowState(width = 500.dp, height = 300.dp)
    ) {
        Column {
            AllTopButtons(1, buttonStates)
//            Box(modifier = Modifier.fillMaxWidth().height(separatorLineWidth).background(Color.Black)) {}
        }
    }
}

@Composable
fun PathField() {

}

@Composable
fun DropDownMenu() {

}

@Composable
fun AllTopButtons(phase: Int, buttonStates: MutableState<MutableList<Boolean>>)/*: MutableList<Boolean>*/ {
    //TODO()
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
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {TopButton(s, index, buttonStates)}
                SeparatorLine()
            }
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {TopButton(values.last(), values.lastIndex, buttonStates)}
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