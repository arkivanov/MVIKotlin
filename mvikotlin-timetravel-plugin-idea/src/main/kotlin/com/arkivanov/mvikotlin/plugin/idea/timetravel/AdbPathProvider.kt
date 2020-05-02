package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.ide.util.PropertiesComponent
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileFilter

internal class AdbPathProvider {

    private val props = PropertiesComponent.getInstance()

    fun get(): String? {
        var path: String? = props.getValue(KEY_ADB_PATH)
        if ((path == null) || !File(path).exists()) {
            val chooser = readOnlyFileChooser()
            chooser.dialogTitle = "Select ADB executable"
            chooser.addChoosableFileFilter(AdbFileFilter())
            chooser.isAcceptAllFileFilterUsed = false

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                path = chooser.selectedFile?.absolutePath
                props.setValue(KEY_ADB_PATH, path)
            }
        }

        return path
    }

    private fun readOnlyFileChooser(): JFileChooser {
        val old = UIManager.getBoolean("FileChooser.readOnly")
        UIManager.put("FileChooser.readOnly", true)
        val chooser = JFileChooser()
        UIManager.put("FileChooser.readOnly", old)

        return chooser
    }

    private companion object {
        private const val KEY_ADB_PATH = "ADB_PATH"
    }

    private class AdbFileFilter : FileFilter() {
        override fun accept(file: File): Boolean = file.isDirectory || (file.name == "adb")

        override fun getDescription(): String = "ADB executable"
    }
}
