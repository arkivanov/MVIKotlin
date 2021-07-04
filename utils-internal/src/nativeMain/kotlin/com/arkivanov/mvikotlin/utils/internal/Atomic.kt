package com.arkivanov.mvikotlin.utils.internal

import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual fun <T> atomic(value: T): AtomicRef<T> =
    object : AtomicRef<T> {
        private val delegate = kotlin.native.concurrent.FreezableAtomicReference(value)

        override var value: T
            get() = delegate.value
            set(value) {
                delegate.value = value.freezeIfNeeded()
            }

        override fun compareAndSet(expected: T, new: T): Boolean = delegate.compareAndSet(expected, new.freezeIfNeeded())

        private fun T.freezeIfNeeded(): T = if (delegate.isFrozen) freeze() else this
    }

actual fun atomic(value: Boolean): AtomicBoolean =
    object : AtomicBoolean {
        private val delegate = kotlin.native.concurrent.AtomicInt(value.toInt())

        override var value: Boolean
            get() = delegate.value != 0
            set(value) {
                delegate.value = value.toInt()
            }

        private fun Boolean.toInt(): Int = if (this) 1 else 0
    }

actual fun atomic(value: Int): AtomicInt =
    object : AtomicInt {
        private val delegate = kotlin.native.concurrent.AtomicInt(value)

        override var value: Int
            get() = delegate.value
            set(value) {
                delegate.value = value
            }
    }

