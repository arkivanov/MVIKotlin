package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import kotlin.js.JsName

/**
 * Represents a source of the `View Events`
 *
 * @see MviView
 */
interface ViewEvents<out Event : Any> {

    /**
     * Subscribes the provided [Observer] of `View Events`.
     * Emissions are performed on the main thread.
     *
     * @param observer an [Observer] that will receive the `View Events`
     */
    @JsName("events")
    @MainThread
    fun events(observer: Observer<Event>): Disposable
}
