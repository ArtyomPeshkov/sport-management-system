package exceptions

import log.*
import ru.emkn.kotlin.sms.Time

class IllegalTimeFormatException(value: Any?) :
    Exception("Ошибка в строковом представлении времени (необходимый формат hh:mm:ss):$YELLOW $value$RESET") {}

class UnexpectedValueException(value: Any?) :
    Exception("Обнаружено неожиданное значение:$YELLOW $value$RESET")

class ProblemWithFilePathException(path: String) :
    Exception("Обнаружена проблема в этом пути:$YELLOW $path$RESET")

class ProblemWithCSVException(path: String) :
    Exception("Проблема с количеством строк или форматом полей файла:$YELLOW $path$RESET")

class SexException(sex: String) :
    Exception("Неправильно уложен пол!:$YELLOW $sex$RESET")

class CSVStringWithNameException(path: String) :
    Exception("Неправильный формат первой строки (только первый столбец должен быть не пустым) в файле:$YELLOW $path$RESET")

class CSVFieldNamesException(path: String) :
    Exception("Неправильно указаны названия столбцов в файле:$YELLOW $path$RESET")

class FileStringException(path: String, index: Int) :
    Exception("Неправильный формат строки номер $index в файле:$YELLOW $path$RESET")

class NegativeSubstractTime(time1: Time, time2: Time) :
        Exception("Попытка вычесть из большего значения меньшее:$YELLOW $time1 и $time2$RESET")