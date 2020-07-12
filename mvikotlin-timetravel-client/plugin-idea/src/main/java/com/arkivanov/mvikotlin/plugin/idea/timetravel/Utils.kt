package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.openapi.ui.Messages
import java.io.BufferedReader
import java.io.IOException

internal fun exec(params: List<String>): Process = Runtime.getRuntime().exec(params.toTypedArray())

@Throws(IOException::class)
internal fun Process.readError(): String? = errorStream?.bufferedReader()?.use(BufferedReader::readText)

internal fun log(text: String) {
    println("[MVIKotlin]: $text")
}

internal fun showErrorDialog(text: String) {
    Messages.showErrorDialog(text, "MVIKotlin")
}

internal fun showInfoDialog(text: String) {
    Messages.showInfoMessage(text, "MVIKotlin")
}
