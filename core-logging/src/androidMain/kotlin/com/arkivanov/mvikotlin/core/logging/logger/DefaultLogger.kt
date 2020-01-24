package com.arkivanov.mvikotlin.core.logging.logger

import android.util.Log

internal actual object DefaultLogger : Logger {

    override fun log(text: String) {
        Log.d("MviKotlin", text)
    }
}
