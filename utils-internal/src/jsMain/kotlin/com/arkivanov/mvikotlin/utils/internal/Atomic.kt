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
