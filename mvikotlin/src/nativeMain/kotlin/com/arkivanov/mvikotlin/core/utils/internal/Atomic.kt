package com.arkivanov.mvikotlin.core.utils.internal

@InternalMviKotlinApi
actual fun <T> atomic(value: T): AtomicRef<T> =
    object : AtomicRef<T> {
        private val delegate = kotlin.concurrent.AtomicReference(value)
        override var value: T by delegate::value

        override fun compareAndSet(expected: T, new: T): Boolean =
            delegate.compareAndSet(expected, new)
    }
