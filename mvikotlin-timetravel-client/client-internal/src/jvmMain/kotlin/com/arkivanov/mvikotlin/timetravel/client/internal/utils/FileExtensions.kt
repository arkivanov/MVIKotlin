package com.arkivanov.mvikotlin.timetravel.client.internal.utils

import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.io.FilenameFilter
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

private val isWindows: Boolean =
    System.getProperty("os.name").startsWith(prefix = "windows", ignoreCase = true)

private val executableExtension: String =
    if (isWindows) ".exe" else ""

private val adbFileName: String = "adb$executableExtension"

fun isValidAdbExecutable(name: String): Boolean =
    name.equals(other = adbFileName, ignoreCase = true)

fun File.isValidAdbExecutable(): Boolean =
    isValidAdbExecutable(name = name)

fun fileDialog(
    title: String,
    mode: FileDialogMode,
    fileFilter: FileFilter? = null,
    selectedFileName: String? = null,
): File? =
    if (isWindows) {
        val dialog = JFileChooser()
        dialog.dialogTitle = title

        dialog.dialogType =
            when (mode) {
                FileDialogMode.OPEN -> JFileChooser.OPEN_DIALOG
                FileDialogMode.SAVE -> JFileChooser.SAVE_DIALOG
            }

        dialog.fileFilter = fileFilter
        dialog.selectedFile = selectedFileName?.let(::File)

        dialog
            .takeIf { it.showDialog(null, null) == JFileChooser.APPROVE_OPTION }
            ?.selectedFile
    } else {
        val dialog =
            FileDialog(
                null as Frame?,
                title,
                when (mode) {
                    FileDialogMode.OPEN -> FileDialog.LOAD
                    FileDialogMode.SAVE -> FileDialog.SAVE
                },
            )

        dialog.filenameFilter = FilenameFilter { path, name -> fileFilter?.accept(File(path, name)) ?: true }
        dialog.file = selectedFileName
        dialog.isVisible = true

        dialog.selectedFile
    }


private val FileDialog.selectedFile: File?
    get() = if ((directory != null) && (file != null)) File(directory, file) else null

enum class FileDialogMode {
    OPEN,
    SAVE,
}
