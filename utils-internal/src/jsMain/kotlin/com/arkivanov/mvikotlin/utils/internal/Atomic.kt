package com.arkivanov.mvikotlin.utils.internal

actual fun <T> atomic(value: T): AtomicRef<T> = AtomicRefImpl(value)

private class AtomicRefImpl<T>(
    override var value: T
) : AtomicRef<T> {
    override fun compareAndSet(expected: T, new: T): Boolean =
        if (this.value === expected) {
            this.value = new
            true
        } else {
            false
        }
}

actual fun atomic(value: Boolean): AtomicBoolean = AtomicBooleanImpl(value)

private class AtomicBooleanImpl(
    override var value: Boolean
) : AtomicBoolean

actual fun atomic(value: Int): AtomicInt = AtomicIntImpl(value)

private class AtomicIntImpl(
    override var value: Int
) : AtomicInt
