package com.arkivanov.mvikotlin.utils.internal

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

typealias AtomicMap<K, V> = AtomicReference<Map<K, V>>

@Suppress("FunctionName")
fun <K, V> AtomicMap(): AtomicMap<K, V> = AtomicMap(emptyMap())

fun <K, V> AtomicMap<K, V>.containsKey(key: K): Boolean = value.containsKey(key)

operator fun <K, V> AtomicMap<K, V>.set(key: K, value: V) {
    update { it.plus(key to value) }
}

operator fun <K, V> AtomicMap<K, V>.minusAssign(key: K) {
    update { it - key }
}

val <K, V> AtomicMap<K, V>.values: Collection<V> get() = value.values

operator fun <K, V> AtomicMap<K, V>.get(key: K): V? = value[key]

val <K, V> AtomicMap<K, V>.size: Int get() = value.size

//fun <K, V> AtomicMap<K, V>.foo()

//val <K, V> AtomicMap<K, V>
