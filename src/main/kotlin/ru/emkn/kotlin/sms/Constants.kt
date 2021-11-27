package ru.emkn.kotlin.sms

import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

enum class Sex {
    MALE, FEMALE, NB
}

enum class Rank {
    MS, CMS, ADULT_FIRST, ADULT_SECOND, ADULT_THIRD, JUNIOR_FIRST, JUNIOR_SECOND, JUNIOR_THIRD
}

