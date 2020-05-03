package com.arkivanov.mvikotlin.timetravel.server

internal class Closeables {

    private var closeables: MutableList<() -> Unit>? = ArrayList()

    @Synchronized
    fun add(closeable: () -> Unit) {
        val closeables = closeables
        if (closeables != null) {
            closeables += closeable
        } else {
            closeable()
            throw InterruptedException()
        }
    }

    fun close() {
        Thread(::closeActual).start()
    }

    @Synchronized
    private fun closeActual() {
        closeables?.forEach { it() }
        closeables = null
    }
}
