package com.arkivanov.mvikotlin.rx.internal

internal inline fun <T> Lock.synchronized(block: () -> T): T {
    lock()

    return try {
        block()
    } finally {
        unlock()
    }
}
