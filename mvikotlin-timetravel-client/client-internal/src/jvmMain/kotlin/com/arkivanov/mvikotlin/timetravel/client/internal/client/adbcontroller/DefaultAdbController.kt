package com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller

import com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller.AdbController.Result
import com.arkivanov.mvikotlin.timetravel.client.internal.utils.isValidAdbExecutable
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import java.io.BufferedReader
import java.io.File
import java.io.IOException

class DefaultAdbController(
    settingsFactory: Settings.Factory,
    private val selectAdbPath: () -> String?
) : AdbController {

    private val storage = settingsFactory.create(name = "AdbPortForwarderSettings")

    override fun forwardPort(port: Int): Result {
        try {
            var adbPath: String? = storage[KEY_ADB_PATH]

            if (adbPath == null || !File(adbPath).isValidAdbExecutable()) {
                adbPath = selectAdbPath() ?: return Result.Error(text = "ADB executable path was not selected")
                storage[KEY_ADB_PATH] = adbPath
            }

            val params = listOf(adbPath, "forward", "tcp:$port", "tcp:$port")
            val process = exec(params)

            if (process.waitFor() != 0) {
                return Result.Error(text = "Failed to forward the port: \"${process.readError()}\"")
            }

            return Result.Success
        } catch (e: Exception) {
            return Result.Error(text = "Failed to forward the port: \"${e.message}\"")
        }
    }

    private fun exec(params: List<String>): Process =
        Runtime.getRuntime().exec(params.toTypedArray())

    @Throws(IOException::class)
    private fun Process.readError(): String? =
        errorStream
            ?.bufferedReader()
            ?.use(BufferedReader::readText)
            ?.trim()

    private companion object {
        private const val KEY_ADB_PATH = "ADB_PATH"
    }

}
