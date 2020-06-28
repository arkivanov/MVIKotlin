package com.arkivanov.mvikotlin.plugin.idea.timetravel

import org.apache.commons.io.filefilter.WildcardFileFilter
import java.awt.FileDialog
import java.io.FileOutputStream
import java.io.IOException

internal object Exporter {

    fun export(data: ByteArray) {
        var path = fileDialog(title = "Save file", mode = FileDialog.SAVE, filenameFilter = WildcardFileFilter("*.tte")) ?: return
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
