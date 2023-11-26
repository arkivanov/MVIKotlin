package com.arkivanov.mvikotlin.core.utils.internal

@OptIn(InternalMviKotlinApi::class) // Don't use this in your project!
internal fun <T> AtomicRef<T>.getAndUpdate(block: (T) -> T): T {
    var oldValue: T
    do {
        oldValue = value
    } while (!compareAndSet(oldValue, block(oldValue)))

    return oldValue
}
