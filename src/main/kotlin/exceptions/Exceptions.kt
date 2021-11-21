package exceptions

import log.*

class ProblemWithFilePath(path: String) :
    Exception("Обнаружена проблема в этом пути:$YELLOW $path$RESET") {}

class ProblemWithCSV(path: String) :
    Exception("Проблема с количеством строк или форматом полей файла:$YELLOW $path$RESET") {}

class SexException(sex: String) :
    Exception("Неправильно уложен пол!:$YELLOW $sex$RESET") {}

class CSVFileStringWithNameException(path: String) :
    Exception("Неправильный формат первой строки (только первый столбец должен быть не пустым) в файле:$YELLOW $path$RESET")

class CSVFileExceptionWithFieldNames(path: String) :
    Exception("Неправильно указаны названия столбцов в файле:$YELLOW $path$RESET") {}

class CollectiveFileStringException(path: String, index: Int) :
    Exception("Неправильный формат строки номер $index в файле:$YELLOW $path$RESET") {}