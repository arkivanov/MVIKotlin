@file:JvmName("AtomicJvm")
@file:Suppress("MatchingDeclarationName")

package com.arkivanov.mvikotlin.utils.internal

import kotlin.jvm.JvmName

interface AtomicRef<T> {

    var value: T

    fun compareAndSet(expected: T, new: T): Boolean
}

expect fun <T> atomic(value: T): AtomicRef<T>

fun <T: Any> atomic(): AtomicRef<T?> = atomic(null)
