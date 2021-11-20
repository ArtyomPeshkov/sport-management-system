package ru.emkn.kotlin.sms

fun main(args: Array<String>) {
    val event = Event(args[0])
    println("${event.name}: ${event.date}")
}
