package log

import org.slf4j.Logger
import ru.emkn.kotlin.sms.Colors
import ru.emkn.kotlin.sms.ControlPointWithTime
import java.lang.reflect.Type

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

