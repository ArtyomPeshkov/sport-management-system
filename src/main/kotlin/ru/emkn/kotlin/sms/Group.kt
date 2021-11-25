package ru.emkn.kotlin.sms

class Group(val groupName: String) {//группа
    var distance: Distance = Distance("") //TODO("Возможно стоит поменять значение по умолчанию")
        set(value) {field=value}
    val listParticipants: MutableList<Participant> = mutableListOf()
}
