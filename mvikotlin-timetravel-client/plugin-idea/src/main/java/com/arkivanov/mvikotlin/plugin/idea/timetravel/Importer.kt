package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import com.intellij.openapi.project.Project
import java.io.FileInputStream
import java.io.IOException

internal class Importer(
    private val project: Project
) {

    fun import(): ByteArray? {
        val path = FileChooser.chooseFile(createSingleFileDescriptor("tte").withTitle("Select file"), project, null)?.path ?: return null

        return try {
            FileInputStream(path).use(FileInputStream::readBytes)
        } catch (e: IOException) {
            showErrorDialog("Error reading file: ${e.message}")
            null
        }
    }
}
