package exceptions

import log.*

class ProblemWithFilePath(path: String) :
    Exception("Обнаружена проблема в этом пути:$YELLOW $path$RESET") {}

class ProblemWithCSV(path: String) :
    Exception("Проблема с количеством строк или форматом полей файла:$YELLOW $path$RESET") {}

class SexException(sex: String) :
    Exception("Неправильно уложен пол!:$YELLOW $sex$RESET") {}

class CollectiveFileStringException(path: String, index: Int = -1) :
    Exception(if (index == -1) "Неправильный формат первой строки с именем коллектива в файле:$YELLOW $path$RESET" else
        "Неправильный формат строки номер $index в файле:$YELLOW $path$RESET") {}