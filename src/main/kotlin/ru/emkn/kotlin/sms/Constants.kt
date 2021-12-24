package ru.emkn.kotlin.sms

import java.time.format.DateTimeFormatter

val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

/** разряд, присуждаемый спортсмену */
enum class Gender {
    MALE, FEMALE, NS //(NS - not stated (не указан))
}

/** разряд, присуждаемый спортсмену */
enum class Rank {
    MS, CMS, ADULT_FIRST, ADULT_SECOND, ADULT_THIRD, JUNIOR_FIRST, JUNIOR_SECOND, JUNIOR_THIRD
}

/** фазы работы программы */
enum class Phase {
    FIRST, SECOND, THIRD
}

/**
 * тип дистанции (учитывается при подсчете контрольных точек):
 * ALL_POINTS означает, что для прохождения дистанции нужно пройти все контрольные точки,
 * SOME_POINTS означает, что для прохождения дистанции необязательно проходить все контрольные точки
 */
enum class DistanceType{
    ALL_POINTS,SOME_POINTS
}

/** цвета (используются в логе) */
enum class Colors(val _name: String) {

    RESET("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m")
}