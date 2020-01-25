package com.arkivanov.mvikotlin.logging.logger

internal actual object DefaultLogger : Logger {

    override fun log(text: String) {
        println("[MviKotlin] $text")
    }
}
