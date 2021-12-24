@file:JvmName("Logs")

package com.arkivanov.mvikotlin.utils.internal

import android.util.Log

@Volatile
private var logger: Logger? = null

private val monitor = Any()

actual fun logV(text: String) {
    ensureLogger().logV(text)
}

actual fun logE(text: String) {
    ensureLogger().logE(text)
}

private fun ensureLogger(): Logger {
    if (logger == null) {
        synchronized(monitor) {
            if (logger == null) {
                logger = createLogger()
            }
        }
    }

    return requireNotNull(logger)
}

private fun createLogger(): Logger = if (isAndroidLoggerAvailable()) AndroidLogger() else SystemLogger()

private fun isAndroidLoggerAvailable(): Boolean =
    try {
        Log.isLoggable("", Log.DEBUG)
        true
    } catch (ignored: Exception) {
        false
    }

private interface Logger {

    fun logV(text: String)

    fun logE(text: String)
}

private class AndroidLogger : Logger {
    override fun logV(text: String) {
        Log.v(LOG_TAG, text)
    }

    override fun logE(text: String) {
        Log.e(LOG_TAG, text)
    }
}

private class SystemLogger : Logger {
    override fun logV(text: String) {
        println(text.withLogTag())
    }

    override fun logE(text: String) {
        System.err.println(text.withLogTag())
    }
}
