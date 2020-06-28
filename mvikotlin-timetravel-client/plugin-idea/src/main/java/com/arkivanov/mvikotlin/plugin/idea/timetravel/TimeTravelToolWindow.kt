package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import javax.swing.JComponent

class TimeTravelToolWindow {

    private val view = TimeTravelView(onConnect = ::onConnect, export = Exporter::export, import = Importer::import)
    private val client = TimeTravelClient(host = "localhost", port = DEFAULT_PORT, view = view)

    val content: JComponent = view.content

    init {
        client.onCreate()
    }

    private fun forwardPort(): Boolean {
        try {
            val adbPath = AdbPathProvider.get() ?: return false
            val params = listOf(adbPath, "forward", "tcp:$DEFAULT_PORT", "tcp:$DEFAULT_PORT")
            val process = exec(params)
            if (process.waitFor() == 0) {
                log("Port forwarded successfully")
                return true
            } else {
                log("Failed to forward the port: ${process.readError()}")
            }
        } catch (e: Exception) {
            log("Failed to forward the port: ${e.message}")
            e.printStackTrace()
        }

        return false
    }

    private fun onConnect(): Boolean {
        if (!forwardPort()) {
            showErrorDialog("Error forwarding port via ADB. Make sure there is only one device connected.")

            return false
        }

        return true
    }
}
