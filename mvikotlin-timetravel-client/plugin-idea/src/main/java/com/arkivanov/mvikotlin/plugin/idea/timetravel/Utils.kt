package com.arkivanov.mvikotlin.plugin.idea.timetravel

import java.awt.FileDialog
import java.io.BufferedReader
import java.io.FilenameFilter
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

internal fun fileDialog(title: String, mode: Int, filenameFilter: FilenameFilter? = null): String? =
    FileDialog(JFrame(), title, mode)
        .apply {
            isAlwaysOnTop = true
            this.filenameFilter = filenameFilter
            isVisible = true
        }
        .file
