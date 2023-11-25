package com.arkivanov.mvikotlin.core.rx.internal

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.utils.internal.InternalMviKotlinApi
import kotlin.js.JsName

@InternalMviKotlinApi
interface Subject<T> : Observer<T> {

    val isActive: Boolean

    @JsName("subscribe")
    fun subscribe(observer: Observer<T>): Disposable

    @MainThread
    override fun onNext(value: T) {
    }

    @MainThread
    override fun onComplete() {
    }
}
