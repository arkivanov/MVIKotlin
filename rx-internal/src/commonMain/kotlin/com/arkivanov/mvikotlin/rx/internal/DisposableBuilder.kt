package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import kotlin.concurrent.Volatile

fun Disposable(onDispose: Disposable.() -> Unit = {}): Disposable =
    object : Disposable {
        @Volatile
        override var isDisposed: Boolean = false

        override fun dispose() {
            isDisposed = true
            onDispose()
        }
    }
