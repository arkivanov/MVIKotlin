package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Observer
import com.badoo.reaktive.utils.atomic.AtomicReference

interface BehaviorSubject<T> : Subject<T> {

    val value: T
}

@Suppress("FunctionName")
fun <T> BehaviorSubject(initialValue: T): BehaviorSubject<T> = BehaviorSubjectImpl(initialValue)

private class BehaviorSubjectImpl<T>(initialValue: T) : ThreadLocalSubject<T>(), BehaviorSubject<T> {

    private val _value = AtomicReference(initialValue)
    override val value: T get() = _value.value

    override fun onSubscribed(observer: Observer<T>) {
        super.onSubscribed(observer)

        observer.onNext(value)
    }

    override fun onNext(value: T) {
        _value.value = value

        super.onNext(value)
    }
}
