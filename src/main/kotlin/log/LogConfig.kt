package log

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.emkn.kotlin.sms.Colors
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Formatter
import java.util.logging.LogRecord

fun Logger.universalC(color: String,s: String, type: Char='d' ) {
    when (type){
        't' -> this.trace(" $color$s${Colors.RESET._name}")
        'd' -> this.debug(" $color$s${Colors.RESET._name}")
        'i' -> this.info(" $color$s${Colors.RESET._name}")
        'w' -> this.warn(" $color$s${Colors.RESET._name}")
        'e' -> this.error(" $color$s${Colors.RESET._name}")
    }
}

fun Logger.printCollection(c:Collection<Any>,color: String,type: Char='d')
{
    c.forEach{
        this.universalC(color,"$it",type)
    }
}

fun Logger.printMap(c:Map<out Any,Any>,color: String,type: Char='d')
{
    c.forEach{
        this.universalC(color,"$it",type)
    }
}

class LogFormatter : Formatter() {
    // Here you can configure the format of the output and
    // its color by using the ANSI escape codes defined above.
    // format is called for every console log message
    override fun format(record: LogRecord): String {
        // This example will print date/time, class, and log level in yellow,
        // followed by the log message and it's parameters in white .
        val builder = StringBuilder()
        builder.append(ANSI_YELLOW)
        builder.append("[")
        builder.append(calcDate(record.millis))
        builder.append("]")
        builder.append(" [")
        builder.append(record.sourceClassName)
        builder.append("]")
        builder.append(" [")
        builder.append(record.level.name)
        builder.append("]")
        builder.append(ANSI_WHITE)
        builder.append(" - ")
        builder.append(record.message)
        val params = record.parameters
        if (params != null) {
            builder.append("\t")
            for (i in params.indices) {
                builder.append(params[i])
                if (i < params.size - 1) builder.append(", ")
            }
        }
        builder.append(ANSI_RESET)
        builder.append("\n")
        return builder.toString()
    }

    private fun calcDate(millisecs: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val resultDate = Date(millisecs)
        return dateFormat.format(resultDate)
    }

    companion object {
        // ANSI escape code
        const val ANSI_RESET = "\u001B[0m"
        const val ANSI_BLACK = "\u001B[30m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
        const val ANSI_PURPLE = "\u001B[35m"
        const val ANSI_CYAN = "\u001B[36m"
        const val ANSI_WHITE = "\u001B[37m"
    }
}