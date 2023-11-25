package com.arkivanov.mvikotlin.core.rx

import kotlin.js.JsName

/**
 * Represents an observer of values
 */
interface Observer<in T> {

    /**
     * Called for every value
     */
    @JsName("onNext")
    fun onNext(value: T)

    /**
     * Called when the stream is completed
     */
    fun onComplete()
}

fun <T> observer(onComplete: (() -> Unit)? = null, onNext: ((T) -> Unit)? = null): Observer<T> =
    ObserverImpl(onComplete, onNext)

private class ObserverImpl<in T>(
    private val onComplete: (() -> Unit)? = null,
    private val onNext: ((T) -> Unit)? = null
) : Observer<T> {
    override fun onNext(value: T) {
        onNext?.invoke(value)
    }

    override fun onComplete() {
        onComplete?.invoke()
    }
}
