package ru.emkn.kotlin.sms


class Participant(
    val surname: String,
    val name: String,
    val sex: Sex,
    val yearOfBirth: Int,
    val rank: String,
    val group: String
) {
    val number: Int
        get() = 0
}