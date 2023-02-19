package com.arkivanov.mvikotlin.logging.logger

/**
 * Represents MVIKotlin logger
 */
fun interface Logger {

    /**
     * Logs the provided string
     *
     * @param text a string to log
     */
    fun log(text: String)
}
