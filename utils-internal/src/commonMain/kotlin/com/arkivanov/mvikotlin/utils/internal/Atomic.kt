@file:JvmName("AtomicJvm")

package com.arkivanov.mvikotlin.utils.internal

import kotlin.jvm.JvmName

interface AtomicRef<T> {

    var value: T

    fun compareAndSet(expected: T, new: T): Boolean
}

interface AtomicBoolean {

    var value: Boolean
}

interface AtomicInt {

    var value: Int
}

expect fun <T> atomic(value: T): AtomicRef<T>

expect fun atomic(value: Boolean): AtomicBoolean

expect fun atomic(value: Int): AtomicInt

fun <T: Any> atomic(): AtomicRef<T?> = atomic(null)
