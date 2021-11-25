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

object Distances {
    var distanceList: MutableList<Distance>  = mutableListOf()//список дистанций
    fun getDistanceByName(name:String): Distance? {
        return distanceList.find {it.name==name}
    }

    /*TODO("Должны считыватья из csv файлов")*/
}

object Collectives {
    var collectiveList: MutableList<Collective>  = mutableListOf()//список дистанций
    /*TODO("Должны считыватья из csv файлов")*/
}

