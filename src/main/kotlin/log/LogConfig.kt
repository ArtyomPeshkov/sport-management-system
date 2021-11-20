package log

import org.slf4j.Logger

const val RESET: String = "\u001B[0m"
const val RED: String = "\u001B[31m"
const val GREEN: String = "\u001B[32m"
const val YELLOW: String = "\u001B[33m"
const val BLUE: String = "\u001B[34m"
const val PURPLE: String = "\u001B[35m"

fun Logger.traceC(s: String) {
    this.debug("$BLUE $s $RESET")
}
fun Logger.debugC(s: String) {
    this.debug("$YELLOW $s $RESET")
}
fun Logger.infoC(s: String) {
    this.debug("$GREEN $s $RESET")
}
fun Logger.warningC(s: String) {
    this.debug("$PURPLE $s $RESET")

}
