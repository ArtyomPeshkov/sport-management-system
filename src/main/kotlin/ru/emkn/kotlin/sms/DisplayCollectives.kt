package ru.emkn.kotlin.sms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Test, TEST, AND ANOTHER TEST",
        state = rememberWindowState(width = 300.dp, height = 300.dp)
    ) {
        val buttonsState =
            remember { mutableStateOf(mapOf("Collective" to "Collective", "Group" to "Group", "Age" to "Age")) }
        val state = remember { mutableStateOf(true) }
        Row(Modifier.fillMaxWidth().padding(start = 5.dp), Arrangement.spacedBy(5.dp)) {
            Button(
                onClick = { state.value = !state.value },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (state.value) Color.Magenta else Color.Red,
                    contentColor = Color.White
                )
            ) {
                buttonsState.value["Collective"]?.let { Text(it) }

            }
            Button(onClick = {}) {
                buttonsState.value["Group"]?.let { Text(it) }
                Color(255, 0, 0)
            }
            Button(onClick = {}) {
                buttonsState.value["Age"]?.let { Text(it) }
                Color(255, 0, 0)
            }
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