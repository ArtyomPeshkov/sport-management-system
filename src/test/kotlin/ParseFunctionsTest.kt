import exceptions.CSVStringWithNameException
import exceptions.ProblemWithCSVException
import ru.emkn.kotlin.sms.NameDate
import ru.emkn.kotlin.sms.formatter
import ru.emkn.kotlin.sms.getNameAndDate
import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class ParseEventTest {

    @Test
    fun `correct input test (event)`() {
        val path = "src/test/resources/events-test/correctEvent.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertEquals(
            NameDate("Mad Wave Classic", LocalDate.parse("20.11.2021", formatter)),
            getNameAndDate(configurationFolder, path)
        )
        tmp.delete()
    }

    @Test
    fun `incorrect header (event)`() {
        val path = "src/test/resources/events-test/incorrectHeader.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<CSVStringWithNameException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `incorrect header two (event)`() {
        val path = "src/test/resources/events-test/incorrectHeaderOther.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<CSVStringWithNameException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `many strings (event)`() {
        val path = "src/test/resources/events-test/tooManyStrings.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `not enough strings (event)`() {
        val path = "src/test/resources/events-test/notEnoughStrings.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }

    @Test
    fun `empty csv (event)`() {
        val path = "src/test/resources/events-test/empty.csv"
        val tmp = File(path).copyTo(File("src/test/resources/events-test/event.csv"), true)
        val configurationFolder = File("src/test/resources/events-test").walk().toList()
        assertFailsWith<ProblemWithCSVException> { getNameAndDate(configurationFolder, path) }
        tmp.delete()
    }
}


internal class ParseCollectiveTest {
//    @Test
//    fun `correct input test (participant)`() {
//        assertEquals(listOf((Participant("М12", Sex.MALE,"АВРАМЕНКО","ДАНИИЛ",2009,"3р"))).toString(), participantsParser("Test",
//            File("src/test/resources/participant-test/correctParticipant.csv")
//        ).second.toString())
//    }
//
//    @Test
//    fun `incorrect header (participant)`() {
//        assertFailsWith<CSVFieldNamesException>{participantsParser("Test", File("src/test/resources/participant-test/incorrectHeader.csv"))}
//    }
//
//
//    @Test
//    fun `empty csv (participant)`() {
//        assertFailsWith<ProblemWithCSVException>{participantsParser("Test", File("src/test/resources/participant-test/empty.csv"))}
//    }
//    @Test
//    fun `not enough strings (participant)`() {
//        assertFailsWith<ProblemWithCSVException>{participantsParser("Test", File("src/test/resources/participant-test/notEnoughStrings.csv"))}
//    }
//
//   val correctApplication:Pair<String, List<Participant>> = Pair("КОМЕТА", listOf())
//
//    @Test
//    fun `correct input test (application)`() {
//        assertEquals(Pair("Mad Wave Classic",  LocalDate.parse("20.11.2021", formatter)), eventParser("src/test/resources/events-test/correctApplication.csv"))
//    }
//
//    @Test
//    fun `incorrect header (application)`() {
//        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/incorrectHeader.csv")}
//    }
//
//    @Test
//    fun `strange csv (application)`() {
//        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/strangeCSVEvent.csv")}
//    }
//
//    @Test
//    fun `many strings (application)`() {
//        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/tooManyStrings.csv")}
//    }
//    @Test
//    fun `not enough strings (application)`() {
//        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/notEnoughStrings.csv")}
//    }

}