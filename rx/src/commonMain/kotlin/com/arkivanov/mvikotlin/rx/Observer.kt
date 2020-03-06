package com.arkivanov.mvikotlin.rx

/**
 * Represents an observer of values
 */
interface Observer<in T> {

    /**
     * Called for every value
     */
    fun onNext(value: T)

    /**
     * Called when the stream is completed
     */
    fun onComplete()
}
