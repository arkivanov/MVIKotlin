package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue

fun Disposable(onDispose: Disposable.() -> Unit = {}): Disposable =
    object : Disposable {
        override var isDisposed: Boolean by atomic(false)

        override fun dispose() {
            isDisposed = true
            onDispose()
        }
    }
