package ru.emkn.kotlin.sms

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.NotEnoughConfigurationFiles
import log.universalC
import java.io.File

fun Group.modifyGroup(
    configFileString: Map<String, String>,
    path: String
) {
   this.addDataWhenInitialise(
        configFileString["ВозрастОт"]?.toInt() ?: throw CSVFieldNamesException(path),
        configFileString["ВозрастДо"]?.toInt() ?: throw CSVFieldNamesException(path),
        chooseSex(configFileString["Пол"] ?: throw CSVFieldNamesException(path))
    )
}

fun getGroups(configurationFolder: List<File>, distanceList: Map<String, Distance>, path: String, currentPhase: Phase) =
    groupsParser(distanceList, configurationFolder.find { it.name.substringAfterLast('/') == "groups.csv" }
        ?: throw NotEnoughConfigurationFiles(path), currentPhase)

fun groupsParser(distanceList: Map<String, Distance>, groups: File, currentPhase: Phase): List<Group> {
    parseLogger.universalC(Colors.BLUE._name, "reading groups from file ${groups.path}", 'i')
    val groupStrings = csvReader().readAllWithHeader(groups)
    return groupStrings.map { groupData ->
        val distance = distanceList[groupData["Дистанция"]] ?: throw CSVFieldNamesException(groups.path)
        val groupName = groupData["Название"] ?: throw CSVFieldNamesException(groups.path)
        val group = Group(groupName, distance)
        if (currentPhase == Phase.FIRST) {
            group.modifyGroup(groupData,groups.path)
        }
        group
    }.toSet().toList()
}