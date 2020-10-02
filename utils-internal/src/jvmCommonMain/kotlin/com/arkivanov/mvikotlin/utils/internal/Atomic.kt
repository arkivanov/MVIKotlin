package com.arkivanov.mvikotlin.utils.internal

actual fun <T> atomic(value: T): AtomicRef<T> =
    object : AtomicRef<T>, java.util.concurrent.atomic.AtomicReference<T>(value) {
        override var value: T
            get() = get()
            set(value) {
                set(value)
            }
    }

actual fun atomic(value: Boolean): AtomicBoolean =
    object : AtomicBoolean, java.util.concurrent.atomic.AtomicBoolean(value) {
        override var value: Boolean
            get() = get()
            set(value) {
                set(value)
            }
    }

actual fun atomic(value: Int): AtomicInt =
    object : AtomicInt, java.util.concurrent.atomic.AtomicInteger(value) {
        override var value: Int
            get() = get()
            set(value) {
                set(value)
            }

        override fun toByte(): Byte = value.toByte()

        override fun toChar(): Char = value.toChar()

        override fun toShort(): Short = value.toShort()
    }
