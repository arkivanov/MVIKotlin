package com.arkivanov.mvidroid.utils

fun <T, S> ModelDiff<T>.diffByEquals(mapper: (T) -> S, consumer: (S) -> Unit) {
    diff(mapper, { newValue, oldValue -> newValue == oldValue }, consumer)
}

fun <T, S> ModelDiff<T>.diffByReference(mapper: (T) -> S, consumer: (S) -> Unit) {
    diff(mapper, { newValue, oldValue -> newValue === oldValue }, consumer)
}
