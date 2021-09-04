package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller.DefaultAdbController
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.TimeTravelClientComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.SettingsConfig
import com.arkivanov.mvikotlin.timetravel.client.internal.utils.isValidAdbExecutable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.subscribe
import com.intellij.openapi.fileChooser.FileChooser.chooseFile
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
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
        val client = client()
        val view = TimeTravelView(TimeTravelViewListener(client))

        var disposable: Disposable? = null
        lifecycle.subscribe(
            onCreate = { disposable = client.models.subscribe(onNext = view::render) },
            onDestroy = {
                disposable?.dispose()
                disposable = null
            }
        )

        return view.content
    }

    @OptIn(ExperimentalSettingsImplementation::class)
    private fun client(): TimeTravelClient {
        val preferencesFactory = JvmPreferencesSettings.Factory(Preferences.userNodeForPackage(PreferencesKey::class.java))

        return TimeTravelClientComponent(
            lifecycle = TimeTravelToolWindowListener.getLifecycle(),
            storeFactory = DefaultStoreFactory(),
            settingsFactory = preferencesFactory,
            settingsConfig = SettingsConfig(
                defaults = SettingsConfig.Defaults(
                    connectViaAdb = true
                )
            ),
            adbController = DefaultAdbController(
                settingsFactory = preferencesFactory,
                selectAdbPath = ::selectAdbPath
            ),
            onImportEvents = ::importEvents,
            onExportEvents = ::exportEvents
        )
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
                .createSaveFileDialog(FileSaverDescriptor("Save file", "MVIKotlin time travel export", "tte"), project)
                .save(null, null)
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
        private val client: TimeTravelClient
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
    }
}
