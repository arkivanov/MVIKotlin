package com.arkivanov.mvikotlin.core.utils.internal

@InternalMviKotlinApi
fun <T : Any> AtomicRef<T?>.initialize(value: T) {
    if (!compareAndSet(null, value)) {
        error("Value is already initialized: $this")
    }
}

@InternalMviKotlinApi
fun <T : Any> AtomicRef<T?>.requireValue(): T =
    requireNotNull(value) { "Value was not initialized" }
