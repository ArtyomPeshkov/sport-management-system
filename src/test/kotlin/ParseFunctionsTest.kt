import exceptions.ProblemWithCSVException
import ru.emkn.kotlin.sms.eventParser
import ru.emkn.kotlin.sms.formatter
import java.time.LocalDate
import kotlin.test.*

internal class ParseFunctionsTest{

    @Test
    fun `correct input test (event)`() {
        assertEquals(Pair("Mad Wave Classic",  LocalDate.parse("20.11.2021", formatter)), eventParser("src/test/resources/events-test/correctEvent.csv"))
    }

    @Test
    fun `incorrect header (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/incorrectHeader.csv")}
    }

    @Test
    fun `strange csv (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/strangeCSVEvent.csv")}
    }

    @Test
    fun `many strings (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/tooManyStrings.csv")}
    }
    @Test
    fun `not enough strings (event)`() {
        assertFailsWith<ProblemWithCSVException>{eventParser("src/test/resources/events-test/notEnoughStrings.csv")}
    }

/*    val correctApplication:Pair<String, List<Participant>> = Pair("КОМЕТА", listOf())

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