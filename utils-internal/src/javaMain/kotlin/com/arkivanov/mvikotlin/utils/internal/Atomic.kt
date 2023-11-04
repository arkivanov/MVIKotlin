package com.arkivanov.mvikotlin.utils.internal

actual fun <T> atomic(value: T): AtomicRef<T> =
    object : AtomicRef<T>, java.util.concurrent.atomic.AtomicReference<T>(value) {
        override var value: T
            get() = get()
            set(value) {
                set(value)
            }
    }
