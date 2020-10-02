package com.arkivanov.mvikotlin.utils.internal

import kotlin.reflect.KProperty

fun <T : Any> AtomicRef<T?>.initialize(value: T) {
    if (!compareAndSet(null, value)) {
        throw IllegalStateException("Value is already initialized: $this")
    }
}

fun <T : Any> AtomicRef<T?>.requireValue(): T = requireNotNull(value) { "Value was not initialized" }

operator fun AtomicBoolean.getValue(thisRef: Any?, property: KProperty<*>): Boolean = value

operator fun AtomicBoolean.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
    this.value = value
}

operator fun AtomicInt.getValue(thisRef: Any?, property: KProperty<*>): Int = value

operator fun AtomicInt.setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
    this.value = value
}

operator fun <T> AtomicRef<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

operator fun <T> AtomicRef<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

fun <T> AtomicRef<T>.getAndUpdate(block: (T) -> T): T {
    var oldValue: T
    do {
        oldValue = value
    } while (!compareAndSet(oldValue, block(oldValue)))

    return oldValue
}
