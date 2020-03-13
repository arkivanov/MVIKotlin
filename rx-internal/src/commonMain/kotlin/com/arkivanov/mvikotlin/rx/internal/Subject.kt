package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlin.native.concurrent.ThreadLocal

interface Subject<T> : Observer<T> {

    val isActive: Boolean

    fun subscribe(observer: Observer<T>): Disposable
}
