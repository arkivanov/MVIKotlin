@file:JvmName("AtomicJvm")
@file:Suppress("MatchingDeclarationName")

package com.arkivanov.mvikotlin.core.utils.internal

import kotlin.jvm.JvmName

@InternalMviKotlinApi
interface AtomicRef<T> {

    var value: T

    fun compareAndSet(expected: T, new: T): Boolean
}

@InternalMviKotlinApi
expect fun <T> atomic(value: T): AtomicRef<T>

@InternalMviKotlinApi
fun <T: Any> atomic(): AtomicRef<T?> = atomic(null)
