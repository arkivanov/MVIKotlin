package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Observer
import kotlin.concurrent.Volatile

interface BehaviorSubject<T> : Subject<T> {

    val value: T
}

@Suppress("FunctionNaming") // https://github.com/detekt/detekt/issues/6601
fun <T> BehaviorSubject(initialValue: T): BehaviorSubject<T> = BehaviorSubjectImpl(initialValue)

private class BehaviorSubjectImpl<T>(initialValue: T) : BaseSubject<T>(), BehaviorSubject<T> {

    @Volatile
    override var value: T = initialValue
        private set

    override fun onAfterSubscribe(observer: Observer<T>) {
        super.onAfterSubscribe(observer)

        observer.onNext(value)
    }

    override fun onBeforeNext(value: T) {
        super.onBeforeNext(value)

        this.value = value
    }
}
