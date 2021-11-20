package ru.emkn.kotlin.sms
import java.util.*

enum class Gender { //пол
    MALE, FEMALE
}

enum class Rank{ //спортивные разряды
    Ю3, Ю2, Ю1, ВЗР3, ВЗР2, ВЗР1, КМС, МС
}

enum class Groups { //список групп
    М18, МЖ9, Ж14, Ж12, М12, М14, Ж21, М60, М16, Ж60, Ж16
}

class Competition (val name: String, val date: Date)

class Participant (
    val name: String, val surname: String,
    val birthYear: Int, val gender: Gender,
    val rank: Rank,  val group: Group) //спортсмен

class Group (val group: Groups,  val distance: Int) //группа

class Collective (val name: String) {

    val athleteList = emptyList<Participant>().toMutableList()

    fun addAthlete(athlete: Participant){
        athleteList.add(athlete)
    }
}

abstract class Protocol

class startProtocol: Protocol()

fun main(args: Array<String>) {
    val event = Event(args[0])
    println("${event.name}: ${event.date}")
}
