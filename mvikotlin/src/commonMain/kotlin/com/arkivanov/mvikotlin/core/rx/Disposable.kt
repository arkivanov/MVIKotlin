package com.arkivanov.mvikotlin.core.rx

import kotlin.concurrent.Volatile

/**
 * Represents a disposable resource
 */
interface Disposable {

    /**
     * Returns whether the instance is disposed or not
     */
    val isDisposed: Boolean

    /**
     * Disposes the instance
     */
    fun dispose()
}

fun Disposable(onDispose: Disposable.() -> Unit = {}): Disposable =
    object : Disposable {
        @Volatile
        override var isDisposed: Boolean = false

        override fun dispose() {
            isDisposed = true
            onDispose()
        }
    }
