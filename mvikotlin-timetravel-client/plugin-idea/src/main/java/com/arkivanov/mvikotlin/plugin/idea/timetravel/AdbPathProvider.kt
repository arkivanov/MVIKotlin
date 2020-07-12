package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.fileChooser.FileChooser.chooseFile
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import java.io.File

internal object AdbPathProvider {

    private val props = PropertiesComponent.getInstance()

    fun get(): String? {
        var path: String? = props.getValue(KEY_ADB_PATH)

        if ((path == null) || !File(path).exists()) {
            showInfoDialog("Please select the adb (Android Debug Bridge) executable file in the next dialog...")

            path =
                chooseFile(
                    createSingleFileDescriptor()
                        .withFileFilter { it.name == "adb" }
                        .withTitle("Select ADB executable"),
                    null,
                    null
                )?.path

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

    private const val KEY_ADB_PATH = "ADB_PATH"
}
