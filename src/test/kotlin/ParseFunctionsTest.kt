import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.ProblemWithCSVException
import exceptions.UnexpectedValueException
import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ParseEventTest {

    @Test
    fun `correct input test (event)`() {
        val path = "src/test/resources/events-test/correctEvent"
        val configurationFolder = File("src/test/resources/events-test/correctEvent").walk().toList()
        assertEquals(
            NameDate("Mad Wave Classic", LocalDate.parse("20.11.2021", formatter)),
            getNameAndDate(configurationFolder, path)
        )
    }

    @Test
    fun `incorrect header (event)`() {
        val path = "src/test/resources/events-test/incorrectHeader"
        val configurationFolder = File("src/test/resources/events-test/incorrectHeader").walk().toList()
        assertFailsWith<CSVStringWithNameException> { getNameAndDate(configurationFolder, path) }
    }

    @Test
    fun `many strings (event)`() {
        val path = "src/test/resources/events-test/tooManyStrings"
        val configurationFolder = File("src/test/resources/events-test/tooManyStrings").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
    }

    @Test
    fun `not enough strings (event)`() {
        val path = "src/test/resources/events-test/notEnoughStrings"
        val configurationFolder = File("src/test/resources/events-test/notEnoughStrings").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
    }

    @Test
    fun `empty csv (event)`() {
        val path = "src/test/resources/events-test/empty"
        val configurationFolder = File("src/test/resources/events-test/empty").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
    }
}

internal class ParseCollectiveTest {
    @Test
    fun `correct input test (participant)`() {
        val path = "src/test/resources/participant-test/correctParticipant.csv"
        assertEquals(
            listOf(Participant(Sex.MALE, "АВРАМЕНКО", "ДАНИИЛ", 2009, "3р")).map { it.toString() },
            participantsParser(csvReader().readAll(readFile(path))[0][0], readFile(path)).map { it.toString() }
        )
    }

    @Test
    fun `incorrect header (participant)`() {
        val path = "src/test/resources/participant-test/incorrectHeader.csv"
        assertFailsWith<CSVFieldNamesException> {
            participantsParser(
                csvReader().readAll(readFile(path))[0][0],
                readFile(path)
            )
        }
    }


    @Test
    fun `empty csv (participant)`() {
        val path = "src/test/resources/participant-test/empty.csv"
        assertFailsWith<ProblemWithCSVException> {
            participantsParser(
                csvReader().readAll(readFile(path))[0][0],
                readFile(path)
            )
        }
    }

    @Test
    fun `not enough strings (participant)`() {
        val path = "src/test/resources/participant-test/notEnoughStrings.csv"
        assertFailsWith<ProblemWithCSVException> {
            participantsParser(
                csvReader().readAll(readFile(path))[0][0],
                readFile(path)
            )
        }
    }

    @Test
    fun `correct input test (multiple participants)`() {
        val answer = listOf(
            Participant(Sex.MALE, "Белов", "Станислав", 2009, ""),
            Participant(Sex.FEMALE, "Треглазова", "Виталия", 2012, "2р"),
            Participant(Sex.FEMALE, "Романенко", "Варвара", 2010, "")
        )
        val path = "src/test/resources/participant-test/correctParticipants.csv"
        assertEquals(
            answer.toString(), participantsParser(csvReader().readAll(readFile(path))[0][0], readFile(path)).toString()
        )
    }
}

internal class ParseDistanceTest {
    @Test
    fun `correct input test (distances)`() {
        val path = "src/test/resources/distances-test/correctDistance"
        val purposeDistance = Distance("D4000", DistanceTypeData(DistanceType.ALL_POINTS, 5, true))
        purposeDistance.addAllPoints(
            listOf(
                ControlPoint("100"),
                ControlPoint("200"),
                ControlPoint("300"),
                ControlPoint("400"),
                ControlPoint("500")
            )
        )
        assertEquals(
            mapOf(Pair("D4000", purposeDistance)).toString(),
            DistanceReader(path).getDistances().toString()
        )
    }

    @Test
    fun `incorrect header (distances)`() {
        val path = "src/test/resources/distances-test/incorrectHeader"
        assertFailsWith<CSVFieldNamesException> { DistanceReader(path).getDistances().toString() }
    }
}

internal class ParseGroupTest {
    @Test
    fun `correct input test (groups)`() {
        val path = "src/test/resources/group-test/correctGroup"
        val purposeDistance = Distance("D4000", DistanceTypeData(DistanceType.ALL_POINTS, 5, true))
        purposeDistance.addAllPoints(
            listOf(
                ControlPoint("100"),
                ControlPoint("200"),
                ControlPoint("300"),
                ControlPoint("400"),
                ControlPoint("500")
            )
        )
        val answer = listOf(Group("М0304", purposeDistance))
        assertEquals(
            answer.toString(),
            GroupReader(path).getGroups(DistanceReader(path).getDistances(), Phase.FIRST).toString()
        )
    }

    @Test
    fun `incorrect header input (groups)`() {
        val path = "src/test/resources/distances-test/incorrectField"
        assertFailsWith<UnexpectedValueException> {
            GroupReader(path).getGroups(
                DistanceReader(path).getDistances(),
                Phase.FIRST
            ).toString()
        }
    }
}