package com.arkivanov.mvikotlin.sample.database

import com.arkivanov.mvikotlin.core.utils.internal.AtomicRef
import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi

@OptIn(InternalMviKotlinApi::class) // Don't use this in your project!
internal fun <T> AtomicRef<T>.getAndUpdate(block: (T) -> T): T {
    var oldValue: T
    do {
        oldValue = value
    } while (!compareAndSet(oldValue, block(oldValue)))

    return oldValue
}
