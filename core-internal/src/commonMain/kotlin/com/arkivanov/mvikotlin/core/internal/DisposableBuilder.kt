package com.arkivanov.mvikotlin.core.internal

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean

inline fun Disposable(crossinline onDispose: () -> Unit): Disposable =
    object : Disposable {
        @Suppress("ObjectPropertyName")
        private val _isDisposed = AtomicBoolean()
        override val isDisposed: Boolean get() = _isDisposed.value

        override fun dispose() {
            _isDisposed.value = true
            onDispose()
        }
    }
