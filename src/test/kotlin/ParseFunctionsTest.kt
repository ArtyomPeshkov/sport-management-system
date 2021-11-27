import exceptions.CSVFieldNamesException
import exceptions.CSVStringWithNameException
import exceptions.FileStringException
import exceptions.ProblemWithCSVException
import ru.emkn.kotlin.sms.*
import java.io.File
import java.time.LocalDate
import kotlin.test.*

internal class ParseFunctionsTest{

    /*@Test
    fun `correct input test (event)`() {
        assertEquals(Pair("Mad Wave Classic",  LocalDate.parse("20.11.2021", formatter)), eventParser("src/test/resources/events-test/correctEvent.csv"))
    }

    @Test
    fun `incorrect header (event)`() {
        assertFailsWith<CSVStringWithNameException>{eventParser("src/test/resources/events-test/incorrectHeader.csv")}
    }

    @Test
    fun `incorrect header two (event)`() {
        assertFailsWith<CSVStringWithNameException>{eventParser("src/test/resources/events-test/incorrectHeaderOther.csv")}
    }

    @Test
    fun `many strings (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/tooManyStrings.csv")}
    }
    @Test
    fun `not enough strings (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/notEnoughStrings.csv")}
    }

    @Test
    fun `empty csv (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/empty.csv")}
    }

    @Test
    fun `correct input test (participant)`() {
        assertEquals(listOf((Participant("М12", Sex.MALE,"АВРАМЕНКО","ДАНИИЛ",2009,"3р"))).toString(), participantsParser("Test",
            File("src/test/resources/participant-test/correctParticipant.csv")
        ).second.toString())
    }

    @Test
    fun `incorrect header (participant)`() {
        assertFailsWith<CSVFieldNamesException>{participantsParser("Test", File("src/test/resources/participant-test/incorrectHeader.csv"))}
    }


    @Test
    fun `empty csv (participant)`() {
        assertFailsWith<ProblemWithCSVException>{participantsParser("Test", File("src/test/resources/participant-test/empty.csv"))}
    }
    @Test
    fun `not enough strings (participant)`() {
        assertFailsWith<ProblemWithCSVException>{participantsParser("Test", File("src/test/resources/participant-test/notEnoughStrings.csv"))}
    }

   val correctApplication:Pair<String, List<Participant>> = Pair("КОМЕТА", listOf())

    @Test
    fun `correct input test (application)`() {
        assertEquals(Pair("Mad Wave Classic",  LocalDate.parse("20.11.2021", formatter)), eventParser("src/test/resources/events-test/correctApplication.csv"))
    }

    @Test
    fun `incorrect header (application)`() {
        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/incorrectHeader.csv")}
    }

    @Test
    fun `strange csv (application)`() {
        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/strangeCSVEvent.csv")}
    }

    @Test
    fun `many strings (application)`() {
        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/tooManyStrings.csv")}
    }
    @Test
    fun `not enough strings (application)`() {
        assertFailsWith<ProblemWithCSV>{eventParser("src/test/resources/events-test/notEnoughStrings.csv")}
    }*/

}