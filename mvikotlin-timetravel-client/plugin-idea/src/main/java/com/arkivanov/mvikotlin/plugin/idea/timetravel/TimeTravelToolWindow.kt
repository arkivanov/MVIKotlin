package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.AdbController
import com.arkivanov.mvikotlin.timetravel.client.internal.client.DefaultConnector
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelClientComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.SettingsConfig
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.integration.TimeTravelSettingsComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.utils.isValidAdbExecutable
import com.badoo.reaktive.disposable.scope.disposableScope
import com.intellij.openapi.fileChooser.FileChooser.chooseFile
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPreferencesSettings
import org.apache.commons.lang.SystemUtils.getUserHome
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.prefs.Preferences
import javax.swing.JComponent

class TimeTravelToolWindow(
    private val project: Project
) {

    fun getContent(lifecycle: Lifecycle): JComponent {
        val components = components()
        val timeTravelView = TimeTravelView(TimeTravelViewListener(components.client, components.settings))
        val settingsView = SettingsView(components.settings)

        disposableScope {
            components.client.models.subscribeScoped(onNext = timeTravelView::render)
            components.settings.models.subscribeScoped(onNext = settingsView::render)
        }.attachTo(lifecycle)

        return timeTravelView.content
    }

    @OptIn(ExperimentalSettingsImplementation::class)
    private fun components(): Components {
        val lifecycle = TimeTravelToolWindowListener.getLifecycle()
        val settingsFactory = JvmPreferencesSettings.Factory(Preferences.userNodeForPackage(PreferencesKey::class.java))

        val settingsComponent =
            TimeTravelSettingsComponent(
                lifecycle = lifecycle,
                storeFactory = DefaultStoreFactory(),
                settingsFactory = settingsFactory,
                settingsConfig = SettingsConfig(
                    defaults = SettingsConfig.Defaults(
                        connectViaAdb = true
                    )
                ),
            )

        val adbController =
            AdbController(
                settingsFactory = settingsFactory,
                selectAdbPath = ::selectAdbPath,
            )

        fun getSettings(): TimeTravelSettings.Model.Settings = settingsComponent.models.value.settings

        val clientComponent =
            TimeTravelClientComponent(
                lifecycle = lifecycle,
                storeFactory = DefaultStoreFactory(),
                connector = DefaultConnector(
                    forwardAdbPort = {
                        val settings = getSettings()
                        if (settings.connectViaAdb) {
                            adbController.forwardPort(port = settings.port)?.let { DefaultConnector.Error(text = it.text) }
                        } else {
                            null
                        }
                    },
                    host = { getSettings().host },
                    port = { getSettings().port },
                ),
                onImportEvents = ::importEvents,
                onExportEvents = ::exportEvents,
            )

        return Components(settingsComponent, clientComponent)
    }

    private fun importEvents(): ByteArray? {
        val path = chooseFile(createSingleFileDescriptor("tte").withTitle("Select file"), project, null)?.path ?: return null

        return try {
            FileInputStream(path).use(FileInputStream::readBytes)
        } catch (e: IOException) {
            showErrorDialog("Error reading file: ${e.message}")
            null
        }
    }

    private fun exportEvents(data: ByteArray) {
        var path =
            FileChooserFactory
                .getInstance()
                .createSaveFileDialog(FileSaverDescriptor("Save File", "MVIKotlin time travel export", "tte"), project)
                .save(null as VirtualFile?, null as String?)
                ?.file
                ?.absolutePath
                ?: return

        if (!path.endsWith(".tte")) {
            path += ".tte"
        }

        try {
            FileOutputStream(path).use { output ->
                output.write(data)
                output.flush()
            }
        } catch (e: IOException) {
            showErrorDialog("Error reading file: ${e.message}")
        }
    }

    private fun selectAdbPath(): String? {
        return chooseFile(
            createSingleFileDescriptor()
                .withFileFilter { it.name == "adb" }
                .withTitle("Select ADB executable"),
            project,
            getUserHome()
                .takeIf(File::exists)
                ?.let { LocalFileSystem.getInstance().findFileByIoFile(it) }
        )?.path
            ?.takeIf { File(it).isValidAdbExecutable() }
    }

    private class TimeTravelViewListener(
        private val client: TimeTravelClient,
        private val settings: TimeTravelSettings,
    ) : TimeTravelView.Listener {
        override fun onConnect() {
            client.onConnectClicked()
        }

        override fun onDisconnect() {
            client.onDisconnectClicked()
        }

        override fun onStartRecording() {
            client.onStartRecordingClicked()
        }

        override fun onStopRecording() {
            client.onStopRecordingClicked()
        }

        override fun onMoveToStart() {
            client.onMoveToStartClicked()
        }

        override fun onStepBackward() {
            client.onStepBackwardClicked()
        }

        override fun onStepForward() {
            client.onStepForwardClicked()
        }

        override fun onMoveToEnd() {
            client.onMoveToEndClicked()
        }

        override fun onCancel() {
            client.onCancelClicked()
        }

        override fun onDebug() {
            client.onDebugEventClicked()
        }

        override fun onExport() {
            client.onExportEventsClicked()
        }

        override fun onImport() {
            client.onImportEventsClicked()
        }

        override fun onEventSelected(index: Int) {
            client.onEventSelected(index = index)
        }

        override fun onSettings() {
            settings.onEditClicked()
        }
    }
}

private class Components(
    val settings: TimeTravelSettings,
    val client: TimeTravelClient,
)
