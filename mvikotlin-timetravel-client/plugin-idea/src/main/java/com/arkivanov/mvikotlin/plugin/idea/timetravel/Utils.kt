package com.arkivanov.mvikotlin.plugin.idea.timetravel

import java.io.BufferedReader
import java.io.IOException
import javax.swing.JFrame
import javax.swing.JOptionPane

internal fun exec(params: List<String>): Process = Runtime.getRuntime().exec(params.toTypedArray())

@Throws(IOException::class)
internal fun Process.readError(): String? = errorStream?.bufferedReader()?.use(BufferedReader::readText)

internal fun log(text: String) {
    println("[MVIKotlin]: $text")
}

internal fun showErrorDialog(text: String) {
    JOptionPane.showMessageDialog(JFrame(), text, "MVIKotlin", JOptionPane.ERROR_MESSAGE)
}
