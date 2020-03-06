package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean

@Suppress("FunctionName")
inline fun Disposable(crossinline onDispose: Disposable.() -> Unit = {}): Disposable =
    object : Disposable {
        @Suppress("ObjectPropertyName")
        private val _isDisposed = AtomicBoolean()
        override val isDisposed: Boolean get() = _isDisposed.value

        override fun dispose() {
            _isDisposed.value = true
            onDispose()
        }
    }
