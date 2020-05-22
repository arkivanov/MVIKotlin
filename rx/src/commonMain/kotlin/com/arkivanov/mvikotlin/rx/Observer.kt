package com.arkivanov.mvikotlin.rx

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
