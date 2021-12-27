package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.desktop.ui.TimeTravelClientUi
import com.arkivanov.mvikotlin.timetravel.client.desktop.ui.theme.TimeTravelClientTheme
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller.DefaultAdbController
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelClientComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.SettingsConfig
import com.arkivanov.mvikotlin.timetravel.client.internal.utils.isValidAdbExecutable
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.russhwolf.settings.JvmPreferencesSettings
import kotlinx.coroutines.Dispatchers
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import java.util.prefs.Preferences

fun main() {
    overrideSchedulers(main = Dispatchers.Main::asScheduler)

    val client =
        invokeOnAwtSync {
            setMainThreadId(Thread.currentThread().id)
            client()
        }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MVIKotlin Time Travel Client",
        ) {
            val settings by client.settings.models.subscribeAsState()

            TimeTravelClientTheme(
                isDarkMode = settings.settings.isDarkMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TimeTravelClientUi(client)
                }
            }
        }
    }
}

private fun client(): TimeTravelClient {
    val lifecycle = LifecycleRegistry()
    val preferencesFactory = JvmPreferencesSettings.Factory(Preferences.userNodeForPackage(PreferencesKey::class.java))

    return TimeTravelClientComponent(
        lifecycle = lifecycle,
        storeFactory = DefaultStoreFactory(),
        settingsFactory = preferencesFactory,
        settingsConfig = SettingsConfig(
            defaults = SettingsConfig.Defaults(
                connectViaAdb = false
            )
        ),
        adbController = DefaultAdbController(
            settingsFactory = preferencesFactory,
            selectAdbPath = ::selectAdbPath
        ),
        onImportEvents = ::importEvents,
        onExportEvents = ::exportEvents
    ).also {
        lifecycle.resume()
    }
}

private fun importEvents(): ByteArray? {
    val dialog = FileDialog(null as Frame?, "MVIKotlin time travel import", FileDialog.LOAD)
    dialog.filenameFilter = FilenameFilter { _, name -> name.endsWith(".tte") }
    dialog.isVisible = true

    return dialog
        .selectedFile
        ?.readBytes()
}

private fun exportEvents(data: ByteArray) {
    val dialog = FileDialog(null as Frame?, "MVIKotlin time travel export", FileDialog.SAVE)
    dialog.file = "TimeTravelEvents.tte"
    dialog.isVisible = true

    dialog
        .selectedFile
        ?.writeBytes(data)
}

/**
 * [FilenameFilter] works on Ubuntu but looks like it doesn't work on MacOS
 */
private fun selectAdbPath(): String? {
    val dialog = FileDialog(null as Frame?, "Select ADB executable path", FileDialog.LOAD)
    dialog.filenameFilter = FilenameFilter { _, name -> name == "adb" }
    dialog.isVisible = true

    return dialog
        .selectedFile
        ?.takeIf(File::isValidAdbExecutable)
        ?.absolutePath
}
