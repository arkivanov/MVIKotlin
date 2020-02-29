package com.arkivanov.mvikotlin.logging.logger

import android.util.Log

actual object DefaultLogger : Logger {

    override fun log(text: String) {
        Log.d("MviKotlin", text)
    }
}
