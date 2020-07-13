package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import java.io.BufferedReader
import java.io.IOException

internal fun exec(params: List<String>): Process = Runtime.getRuntime().exec(params.toTypedArray())

@Throws(IOException::class)
internal fun Process.readError(): String? =
    errorStream
        ?.bufferedReader()
        ?.use(BufferedReader::readText)
        ?.trim()

internal fun logI(text: String) {
    Logger.getInstance("MVIKotlin").info(text)
}

internal fun logE(text: String, e: Throwable? = null) {
    Logger.getInstance("MVIKotlin").error(text, e)
}

internal fun showErrorDialog(text: String) {
    Messages.showErrorDialog(text, "MVIKotlin")
}

internal fun showInfoDialog(text: String) {
    Messages.showInfoMessage(text, "MVIKotlin")
}
