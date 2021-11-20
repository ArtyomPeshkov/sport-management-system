package ru.emkn.kotlin.sms

import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

enum class Sex {
    MALE, FEMALE
}

enum class Rank {
    MS, CMS, ADULT_FIRST, ADULT_SECOND, ADULT_THIRD, JUNIOR_FIRST, JUNIOR_SECOND, JUNIOR_THIRD
}

enum class Groups { //список групп
    M18, MF9, F14, F12, M12, M14, F21, M60, M16, F60, F16
}