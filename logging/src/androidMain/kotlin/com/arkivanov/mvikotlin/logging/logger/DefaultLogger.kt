package com.arkivanov.mvikotlin.logging.logger

import android.util.Log

/**
 * A default implementation of the [Logger], uses `LogCat`
 */
actual object DefaultLogger : Logger {

    override fun log(text: String) {
        Log.d("MVIKotlin", text)
    }
}
