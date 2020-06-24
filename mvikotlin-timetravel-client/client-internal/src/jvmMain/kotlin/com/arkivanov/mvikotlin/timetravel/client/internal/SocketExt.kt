package com.arkivanov.mvikotlin.timetravel.client.internal

import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

internal fun Socket.closeSafe() {
    try {
        close()
    } catch (ignored: IOException) {
    }
}

internal fun Socket.closeSafeAsync() {
    thread(block = ::closeSafe)
}
