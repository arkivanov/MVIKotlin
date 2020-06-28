package com.arkivanov.mvikotlin.plugin.idea.timetravel

import org.apache.commons.io.filefilter.WildcardFileFilter
import java.awt.FileDialog
import java.io.FileInputStream
import java.io.IOException

internal object Importer {

    fun import(): ByteArray? {
        val path = fileDialog(title = "Select file", mode = FileDialog.LOAD, filenameFilter = WildcardFileFilter("*.tte")) ?: return null

        return try {
            FileInputStream(path).use(FileInputStream::readBytes)
        } catch (e: IOException) {
            showErrorDialog("Error reading file: ${e.message}")
            null
        }
    }
}
