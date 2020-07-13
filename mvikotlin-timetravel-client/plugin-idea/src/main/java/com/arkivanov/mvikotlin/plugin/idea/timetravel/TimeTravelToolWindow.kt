package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import javax.swing.JComponent

internal class TimeTravelToolWindow(
    private val adbPathProvider: AdbPathProvider,
    exporter: Exporter,
    importer: Importer
) {

    private val view = TimeTravelView(onConnect = ::onConnect, export = exporter::export, import = importer::import)
    private val client = TimeTravelClient(host = "localhost", port = DEFAULT_PORT, view = view)

    val content: JComponent = view.content

    init {
        client.onCreate()
    }

    private fun onConnect(): Boolean {
        try {
            val adbPath = adbPathProvider.get()

            if (adbPath == null) {
                showError("ADB executable path was not selected")
                return false
            }

            logI("Using ADB path: $adbPath")

            val params = listOf(adbPath, "forward", "tcp:$DEFAULT_PORT", "tcp:$DEFAULT_PORT")
            val process = exec(params)
            if (process.waitFor() == 0) {
                logI("Port forwarded successfully")
                return true
            }

            showError("Failed to forward the port: \"${process.readError()}\"")
        } catch (e: Exception) {
            showError("Failed to forward the port: \"${e.message}\"", e)
        }

        return false
    }

    private fun showError(text: String, e: Throwable? = null) {
        logE(text, e)
        showErrorDialog(text)
    }
}
