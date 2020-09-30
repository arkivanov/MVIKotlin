package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue

@Suppress("FunctionName")
inline fun Disposable(crossinline onDispose: Disposable.() -> Unit = {}): Disposable =
    object : Disposable {
        @Suppress("ObjectPropertyName")
        override var isDisposed: Boolean by atomic(false)

        override fun dispose() {
            isDisposed = true
            onDispose()
        }
    }
