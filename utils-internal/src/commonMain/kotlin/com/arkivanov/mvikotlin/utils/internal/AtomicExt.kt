package com.arkivanov.mvikotlin.utils.internal

fun <T : Any> AtomicRef<T?>.initialize(value: T) {
    if (!compareAndSet(null, value)) {
        error("Value is already initialized: $this")
    }
}

fun <T : Any> AtomicRef<T?>.requireValue(): T = requireNotNull(value) { "Value was not initialized" }

fun <T> AtomicRef<T>.getAndUpdate(block: (T) -> T): T {
    var oldValue: T
    do {
        oldValue = value
    } while (!compareAndSet(oldValue, block(oldValue)))

    return oldValue
}
