package ru.emkn.kotlin.sms
import log.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory


val logger1: Logger = LoggerFactory.getLogger("asdad")
fun main(args: Array<String>) {
    logger1.infoC("Hello world")
    logger1.debugC ("Something expensive to compute")
    try {
        println(1/0)
    } catch(exc: Exception) {
        logger1.error (exc.message)
    }

}