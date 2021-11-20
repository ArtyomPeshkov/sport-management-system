package ru.emkn.kotlin.sms
import java.util.*

enum class Gender { //пол
    Male, Female
}

enum class Rank{ //спортивные разряды
    Ю3, Ю2, Ю1, ВЗР3, ВЗР2, ВЗР1, КМС, МС
}

enum class Groups { //список групп
    М18, МЖ9, Ж14, Ж12, М12, М14, Ж21, М60, М16, Ж60, Ж16
}

class Competition (val name: String, val date: Date)

class Participant (val name: String, val surname: String, val birthYear: Int, val gender: Gender) //спортсмен

class Group (val group: Groups,  val distance: Int) //группа

class Collective (val name: String) {

    val athleteList = emptyList<Triple<Participant, Rank, Group>>().toMutableList()

    fun addAthlete(athlete: Participant, rank: Rank, group: Group){
        athleteList.add(Triple(athlete, rank, group))
    }
}

abstract class Protocol

fun main(args: Array<String>) {
    TODO()
}
