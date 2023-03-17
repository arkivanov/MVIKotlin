package com.arkivanov.mvikotlin.logging.logger

/**
 * Represents MVIKotlin logger
 */
interface Logger {

    /**
     * Logs the provided string
     *
     * @param text a string to log
     */
    fun log(text: String)
}
