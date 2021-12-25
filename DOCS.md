# Система для проведения спортивных соревнований

## Инструкции по использованию программы

### Структура программы

Программа поддерживает три режима работы (три фазы):
1. По заявочным спискам формируются стартовые протоколы. 
2. По стартовым протоколам и протоколам прохождения контрольных пунктов формируются протоколы результатов.
3. По протоколам результатов формируется протокол результатов для команд.

### Требования к файлам

Все файлы должны находиться в папке *csvFiles/configuration*.

Папки:
1. В папке *applications* должны хранится файлы с заявочными протоколами (название файла должно иметь вид *application\*.csv*, где * - это номер заявочного протокола (нумерация должна начинаться с единицы)).   
2. В папке *points* должны хранится файлы с контрольными точками (название файла должно иметь вид *control-point_\*.csv*, где * - это название (номер) контрольной точки).
3. В папке *results* должны хранится файлы с результатами для групп (название файла должно иметь вид *result_\*.csv*, где * - это название группы).
4. В папке *starts* должны хранится файлы со стартовыми протоколами (название файла должно иметь вид *start_\*.csv*, где * - это название группы).   

Файлы:
1. В файле *distances.csv* должно хранится соответствие дистанций со списком контрольных точек, типом прохождения дистанций и тд (подробнее далее)
2. В файле *event.csv* должны хранится название и дата проводимых соревнований
3. В файле *groups.csv* должно хранится соответствие групп с дистанциями, полом и возрастом
4. В файле *teamsResults.csv* должны хранится результаты для команд (формируются в результате работы третьей фазы)

### Примеры файлов

**Пример заявочного протокола (*application\*.csv*):**

```csv
КОМЕТА,,,,
Группа,Фамилия,Имя,Пол,Г.р.,Разр.
М0304,БЕЛОВ,СТАНИСЛАВ,М,2003,
Ж1012,ТРЕГЛАЗОВА,ВИТАЛИЯ,Ж,2012,
Ж1012,РОМАНЕНКО,ВАРВАРА,Ж,2010,
Ж0506,ФАДЕЕВА,ПОЛИНА,Ж,2006,
М1012,ПЕТРОВ,ИВАН,М,2010,
М0709,ЛИПАТОВ,ОЛЕГ,М,2009,
М1012,СИМОНЯН,НИКИТА,М,2011,
```

**Пример файла для контрольной точки (*control-point_\*.csv*):**

```csv
100,
121,12:01:11
103,12:02:13
113,12:03:20
122,12:04:10
126,12:05:15
120,12:06:12
118,12:07:13
116,12:08:10
117,12:09:19
119,12:10:19
```
*_в данном файле 100 - название контрольной точки_*

**Пример файла с результатом для группы (*result_\*.csv*):**

```csv
Ж0304,,,,,,,,,,
Порядковый номер,Номер,Фамилия,Имя,Пол,Г.р.,Коллектив,Разр.,Результат,Место,Отставание
1,511,Егорченко,АЛИНА,FEMALE,2003,КОМЕТА,,09:23,1,
2,502,АЛЕКСЕЕВА,ЛЮБОВЬ,FEMALE,2004,ЭКРАН,,09:35,2,+00:12
3,525,МИХАЙЛОВА,ТАТЬЯНА,FEMALE,2003,ВЕЛИКИЕ ЛУКИ,,09:50,3,+00:27
4,501,ФИЛИППОВА,МЕЛИССА,FEMALE,2004,ШКОЛА №3,,09:52,4,+00:29
```

**Пример стартового протокола (*start_\*.csv*):**

```csv
Ж0304,,,,,,,
Номер,Фамилия,Имя,Пол,Г.р.,Коллектив,Разр.,Стартовое время
511,Егорченко,АЛИНА,FEMALE,2003,КОМЕТА,,12:19:00
524,ИВАНОВА,ДИАНА,FEMALE,2003,ЭКРАН,,12:20:00
502,АЛЕКСЕЕВА,ЛЮБОВЬ,FEMALE,2004,ЭКРАН,,12:21:00
501,ФИЛИППОВА,МЕЛИССА,FEMALE,2004,ШКОЛА №3,,12:22:00
```

**Пример файла с дистанциями (*distances.csv*):**

```csv
Название,Тип,Количество точек,Порядок,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40
D4000,all,any,1,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2100,2200,2300,2400,2500,2600,2700,2800,2900,3000,3100,3200,3300,3400,3500,3600,3700,3800,3900,4000
D2000,all,all,1,100,200,300,400,500,600,700,800,900,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,,,,,,,,,,,,,,,,,,,,
D1000,all,hi,1,100,200,300,400,500,600,700,800,900,1000,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
D500s,some,5,0,100,200,300,400,500,100,200,300,400,500,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
```
*_в данном файле есть два типа: all/ALL_POINTS (означает, что для прохождения дистанции нужно пройти все контрольные точки) и some/SOME_POINTS (означает, что для прохождения дистанции необязательно проходить все контрольные точки); 
если указан тип some/SOME_POINTS, то необходимо указать количество точек (цифрой), которое необходимо преодолеть для прохождения дистанции, если же указан тип all/ALL_POINTS, то в данном поле может лежать любой объект не содержащий запятую; 
в столбце порядок может стоять 1/0 в зависимости от того, стоит учитывать порядок прохождения контрольных точек или нет; 
контрольные точки должны быть выписаны в том порядке в котором участникам необходимо их проходить_*

**Пример файла *event.csv*:**

```csv
Название,Дата
Mad Wave Classic,20.11.2021
```

**Пример файла с группами (*groups.csv*):**

```csv
Название,Дистанция,Пол,ВозрастОт,ВозрастДо
М0304,D4000,М,17,18
М0506,D2000,М,15,16
М0709,D1000,М,12,14
М1012,D500a,М,9,11
```

**Пример файла с результатами для группы (*teamsResults.csv*):**

```csv
Коллектив,Баллы
ВЕЛИКИЕ ЛУКИ,2625

Участник,Баллы
ОРЛОВ ЮРИЙ,100
ОРЛОВ ВАДИМ,92
ПИВОВАРОВ ВЛАДИСЛАВ,88
БЕЛОВ АНДРЕЙ,84
ЯКОВЛЕВ ИВАН,94
ПИСКУНОВ ВЛАДИМИР,92
МЕЛЬНИКОВ ИВАН,89
ВИНОГРАДОВ АРТЕМ,88
...
```

### Список файлов/папок, необходимых для корректной работы программы

**1 фаза:**
1. distances.csv
2. groups.csv
3. applications/
4. event.csv

**2 фаза:**
1. distances.csv
2. starts/
3. points/

**3 фаза:**
1. results/