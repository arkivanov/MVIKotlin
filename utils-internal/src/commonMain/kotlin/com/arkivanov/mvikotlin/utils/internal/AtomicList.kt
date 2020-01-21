package com.arkivanov.mvikotlin.utils.internal

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

typealias AtomicList<T> = AtomicReference<List<T>>

@Suppress("FunctionName")
fun <T> AtomicList(): AtomicList<T> = AtomicList(emptyList())

fun <T> AtomicList<T>.add(element: T) {
    update { it + element }
}

operator fun <T> AtomicList<T>.plusAssign(element: T) {
    add(element)
}

fun <T> AtomicList<T>.remove(element: T): Boolean {
    var removed = false

    update {
        val newList = it - element
        removed = newList.size < it.size
        newList
    }

    return removed
}

operator fun <T> AtomicList<T>.minusAssign(element: T) {
    remove(element)
}

fun <T> AtomicList<T>.clear() {
    update { emptyList() }
}

operator fun <T> AtomicList<T>.get(index: Int): T = value[index]

fun <T> AtomicList<T>.firstOrNull(): T? = value.firstOrNull()

val AtomicReference<out Collection<*>>.size: Int get() = value.size

val AtomicReference<out Collection<*>>.isEmpty: Boolean get() = value.isEmpty()

val AtomicReference<out Collection<*>>.isNotEmpty: Boolean get() = value.isNotEmpty()

operator fun <T> AtomicReference<out Iterable<T>>.iterator(): Iterator<T> = value.iterator()
