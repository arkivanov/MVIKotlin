package com.arkivanov.mvikotlin.logging.logger

/**
 * A default implementation of the [Logger], uses `console`
 */
actual object DefaultLogger : Logger {

    override fun log(text: String) {
        console.log("MVIKotlin] $text")
    }
}
