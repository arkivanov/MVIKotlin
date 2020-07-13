package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.fileChooser.FileChooser.chooseFile
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import com.intellij.openapi.project.Project
import java.io.File

internal class AdbPathProvider(
    private val project: Project
) {

    private val props = PropertiesComponent.getInstance()

    fun get(): String? {
        var path: String? = props.getValue(KEY_ADB_PATH)
        logI("ADB path in props: $path")

        if ((path == null) || !File(path).exists()) {
            showInfoDialog("Please select the adb (Android Debug Bridge) executable file in the next dialog...")

            logI("Showing ADB file chooser")

            path =
                chooseFile(
                    createSingleFileDescriptor()
                        .withFileFilter { it.name == "adb" }
                        .withTitle("Select ADB executable"),
                    project,
                    null
                )?.path

            logI("Selected ADB path: $path")

            if (path != null) {
                if (File(path).name != "adb") {
                    showErrorDialog("The selected file should be an adb executable, you selected: $path")
                    path = null
                } else {
                    props.setValue(KEY_ADB_PATH, path)
                }
            }
        }

        return path
    }

    private companion object {
        private const val KEY_ADB_PATH = "ADB_PATH"
    }
}
