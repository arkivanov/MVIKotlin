package com.arkivanov.mvikotlin.logging.logger

import com.arkivanov.mvikotlin.utils.internal.logV

/**
 * A default implementation of the [Logger]
 */
object DefaultLogger : Logger {

    override fun log(text: String) {
        logV(text)
    }
}
