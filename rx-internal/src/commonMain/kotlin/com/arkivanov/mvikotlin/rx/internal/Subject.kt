package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import kotlin.js.JsName

interface Subject<T> : Observer<T> {

    val isActive: Boolean

    @JsName("subscribe")
    fun subscribe(observer: Observer<T>): Disposable

    // @MainThread
    override fun onNext(value: T) {
    }

    // @MainThread
    override fun onComplete() {
    }
}
