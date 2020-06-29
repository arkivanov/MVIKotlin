package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor
import java.io.FileInputStream
import java.io.IOException

internal object Importer {

    fun import(): ByteArray? {
        var path = FileChooser.chooseFile(createSingleFileDescriptor("tte").withTitle("Select file"), null, null)?.path ?: return null

        return try {
            FileInputStream(path).use(FileInputStream::readBytes)
        } catch (e: IOException) {
            showErrorDialog("Error reading file: ${e.message}")
            null
        }
    }
}
