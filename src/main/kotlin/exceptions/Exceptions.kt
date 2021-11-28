package exceptions

import log.*
import ru.emkn.kotlin.sms.Colors
import ru.emkn.kotlin.sms.Time

class  IncorrectControlPointValue(value: String) :
    Exception("Название контрольной точки должно быть числовым. Ваше название: ${Colors.YELLOW.name} $value${Colors.RESET}")

class IllegalTimeFormatException(value: Any?) :
    Exception("Ошибка в строковом представлении времени (необходимый формат hh:mm:ss):${Colors.YELLOW.name} $value${Colors.RESET}") {}

class UnexpectedValueException(value: Any?) :
    Exception("Обнаружено неожиданное значение:${Colors.YELLOW.name} $value${Colors.RESET}")

class ProblemWithFilePathException(path: String) :
    Exception("Обнаружена проблема в этом пути:${Colors.YELLOW.name} $path${Colors.RESET}")

class ProblemWithCSVException(path: String) :
    Exception("Проблема с количеством строк или форматом полей файла:${Colors.YELLOW.name} $path${Colors.RESET}")

class NotEnoughConfigurationFiles(path: String) :
    Exception("Отсутствуют необходимые конфигурационные файлы, в READ.me указаны все необходимые файлы для различных режимов работы. Проблема в папке: ${Colors.YELLOW.name} $path${Colors.RESET}")

class SexException(sex: String) :
    Exception("Неправильно уложен пол!:${Colors.YELLOW.name} $sex${Colors.RESET}")

class CSVStringWithNameException(path: String) :
    Exception("Неправильный формат первой строки (только первый столбец должен быть не пустым) в файле:${Colors.YELLOW.name} $path${Colors.RESET}")

class CSVFieldNamesException(path: String) :
    Exception("Неправильно указаны названия столбцов в файле:${Colors.YELLOW.name} $path${Colors.RESET}")

class FileStringException(path: String, index: Int) :
    Exception("Неправильный формат строки номер $index в файле:${Colors.YELLOW.name} $path${Colors.RESET}")

class NegativeSubstractTime(time1: Time, time2: Time) :
        Exception("Попытка вычесть из большего значения меньшее:${Colors.YELLOW.name} $time1 и $time2${Colors.RESET}")