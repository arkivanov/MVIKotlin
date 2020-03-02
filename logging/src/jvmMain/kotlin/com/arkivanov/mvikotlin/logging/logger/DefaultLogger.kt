package com.arkivanov.mvikotlin.logging.logger

/**
 * A default implementation of the [Logger]
 */
actual object DefaultLogger : Logger {

    override fun log(text: String) {
        println("[MVIKotlin] $text")
    }
}
