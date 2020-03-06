package com.arkivanov.mvikotlin.utils.internal

import com.badoo.reaktive.utils.atomic.AtomicReference

// FIXME: rename to lateinit
fun <T : Any> lateinitAtomicReference(): AtomicReference<T?> = AtomicReference(null)

fun <T : Any> AtomicReference<T?>.initialize(value: T) {
    check(this.value == null) { "AtomicReference is already initialized: $this" }

    this.value = value
}

val <T : Any> AtomicReference<T?>.requireValue: T
    get() = requireNotNull(value) { "Value was not initialized" }
