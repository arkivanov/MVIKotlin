package com.arkivanov.mvikotlin.rx.internal

@Suppress("EmptyDefaultConstructor")
internal expect class Lock() {

    fun lock()

    fun unlock()
}
