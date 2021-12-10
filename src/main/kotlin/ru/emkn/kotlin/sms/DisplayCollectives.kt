package ru.emkn.kotlin.sms

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

data class ButtonState(var backgroundColor: Color = Color(255, 255, 255)) {

}

@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test, TEST, AND ANOTHER TEST",
        state = rememberWindowState(width = 500.dp, height = 300.dp)
    ) {
        val buttonsState =
            remember { mutableStateOf(mapOf("Collective" to "Collective", "Group" to "Group", "Age" to "Age")) }
        val state = remember { mutableStateOf(true) }
        Row(Modifier.fillMaxSize(), Arrangement.spacedBy(0.dp)) {
//            @OptIn(ExperimentalFoundationApi::class)
            Box(
                Modifier.weight(1f).clickable { state.value = !state.value }
                    .background(if (state.value) Color.Magenta else Color.Red).wrapContentSize()
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                buttonsState.value["Collective"]?.let { Text(it) }
            }
            Box(
                Modifier.weight(1f).clickable { state.value = !state.value }
                    .background(if (state.value) Color.Magenta else Color.Red).wrapContentSize()
            ) {
                buttonsState.value["Group"]?.let { Text(it) }
            }
            Box( //TODO("Чтобы отслеживать правые клики вместо clickable надо mouseClicked, но затемнение надо будет добавить самому. Для отслеживания buttons..")
                Modifier.weight(1f).clickable { state.value = !state.value }
                    .background(if (state.value) Color.Magenta else Color.Red).wrapContentSize()

            ) {
                buttonsState.value["Age"]?.let { Text(it) }
            }
//            Button(
//                modifier = Modifier.weight(1f).fillMaxWidth(),
//                onClick = { state.value = !state.value },
//                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = if (state.value) Color.Magenta else Color.Red,
//                    contentColor = Color.White
//                )
//            ) {
//                buttonsState.value["Collective"]?.let { Text(it) }
//
//            }
////            Box(modifier = Modifier.width(20.dp).fillMaxHeight().background(Color(128, 128, 128)))
//            Button(modifier = Modifier.weight(1f).fillMaxWidth(), onClick = {}) {
//                buttonsState.value["Group"]?.let { Text(it) }
//                Color(255, 0, 0)
//            }
//            Button(modifier = Modifier.weight(1f).fillMaxWidth(), onClick = {}) {
//                buttonsState.value["Age"]?.let { Text(it) }
//                Color(255, 0, 0)
//            }
        }


//        val count = remember { mutableStateOf(0) }
//        val stateVertical = rememberScrollState(0)
//        val stateHorizontal = rememberScrollState(0)
//        Column(
//            Modifier.fillMaxSize(),
//            Arrangement.spacedBy(5.dp)
//        ) { //Тут дп для отступов между строками в столбце. Он обычно самый большой контейнер
//            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
//                onClick = {
//                    count.value++
//                }) {
//                Text(if (count.value == 0) "Hello World" else "Clicked ${count.value}!")
//            }
//
//            Button(modifier = Modifier.align(Alignment.CenterHorizontally),
//                onClick = {
//                    count.value = 0
//                }) {
//                Text("Reset")
//            }
//
//
//        }
    }
}

@Composable
fun TopButton(text: String) {

}