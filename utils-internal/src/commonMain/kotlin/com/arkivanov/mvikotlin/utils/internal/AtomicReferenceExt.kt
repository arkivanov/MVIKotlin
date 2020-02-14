package com.arkivanov.mvikotlin.utils.internal

import com.badoo.reaktive.utils.atomic.AtomicReference

// FIXME: rename to lateinit
fun <T : Any> lazyAtomicReference(): AtomicReference<T?> = AtomicReference(null)

val <T : Any> AtomicReference<T?>.requireValue: T
    get() = requireNotNull(value) { "Value was not initialized" }
