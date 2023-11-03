package com.arkivanov.mvikotlin.utils.internal

actual fun <T> atomic(value: T): AtomicRef<T> =
    object : AtomicRef<T> {
        private val delegate = kotlin.concurrent.AtomicReference(value)
        override var value: T by delegate::value

        override fun compareAndSet(expected: T, new: T): Boolean =
            delegate.compareAndSet(expected, new)
    }

actual fun atomic(value: Boolean): AtomicBoolean =
    object : AtomicBoolean {
        private val delegate = kotlin.concurrent.AtomicInt(value.toInt())

        override var value: Boolean
            get() = delegate.value != 0
            set(value) {
                delegate.value = value.toInt()
            }

        private fun Boolean.toInt(): Int = if (this) 1 else 0
    }

actual fun atomic(value: Int): AtomicInt =
    object : AtomicInt {
        private val delegate = kotlin.concurrent.AtomicInt(value)
        override var value: Int by delegate::value
    }

