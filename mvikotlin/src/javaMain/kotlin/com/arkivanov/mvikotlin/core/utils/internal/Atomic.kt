package com.arkivanov.mvikotlin.core.utils.internal

@InternalMviKotlinApi
actual fun <T> atomic(value: T): AtomicRef<T> =
    object : AtomicRef<T>, java.util.concurrent.atomic.AtomicReference<T>(value) {
        override var value: T
            get() = get()
            set(value) {
                set(value)
            }
    }
