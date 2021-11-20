package ru.emkn.kotlin.sms

import mu.KotlinLogging

val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
    logger.info { "Hello world" }
    logger.debug { "Something expensive to compute" }
    try {
        println(1/0)
    } catch(exc: Exception) {
        logger.error { exc.message }
    }

}
