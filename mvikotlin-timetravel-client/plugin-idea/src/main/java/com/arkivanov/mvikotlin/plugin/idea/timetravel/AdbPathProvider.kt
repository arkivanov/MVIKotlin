package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.ide.util.PropertiesComponent
import org.apache.commons.io.filefilter.NameFileFilter
import java.awt.FileDialog
import java.io.File

internal object AdbPathProvider {

    private val props = PropertiesComponent.getInstance()

    fun get(): String? {
        var path: String? = props.getValue(KEY_ADB_PATH)
        if ((path == null) || !File(path).exists()) {
            path = fileDialog(title = "Select ADB executable", mode = FileDialog.LOAD, filenameFilter = NameFileFilter("adb"))
            if (path != null) {
                props.setValue(KEY_ADB_PATH, path)
            }
        }

        return path
    }

    private const val KEY_ADB_PATH = "ADB_PATH"
}
