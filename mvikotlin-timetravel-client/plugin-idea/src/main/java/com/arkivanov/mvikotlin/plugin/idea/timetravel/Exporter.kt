package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.FileOutputStream
import java.io.IOException

internal class Exporter(
    private val project: Project
) {

    fun export(data: ByteArray) {
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
}
