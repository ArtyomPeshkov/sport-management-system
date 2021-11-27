package ru.emkn.kotlin.sms

import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

enum class Sex {
    MALE, FEMALE
}

enum class Rank {
    MS, CMS, ADULT_FIRST, ADULT_SECOND, ADULT_THIRD, JUNIOR_FIRST, JUNIOR_SECOND, JUNIOR_THIRD
}

object Groups {
    var groupList: MutableList<Group>  = mutableListOf()//список групп

    /*TODO("Должны считыватья из csv файлов")*/
}

