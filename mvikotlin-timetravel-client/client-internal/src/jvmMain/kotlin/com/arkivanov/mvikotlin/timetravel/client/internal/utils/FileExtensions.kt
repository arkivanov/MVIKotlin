package com.arkivanov.mvikotlin.timetravel.client.internal.utils

import java.io.File

fun File.isValidAdbExecutable(): Boolean = nameWithoutExtension == "adb"
