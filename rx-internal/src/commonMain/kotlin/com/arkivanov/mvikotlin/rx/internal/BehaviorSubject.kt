package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue

interface BehaviorSubject<T> : Subject<T> {

    val value: T
}

@Suppress("FunctionName")
fun <T> BehaviorSubject(initialValue: T): BehaviorSubject<T> = BehaviorSubjectImpl(initialValue)

private class BehaviorSubjectImpl<T>(initialValue: T) : ThreadLocalSubject<T>(), BehaviorSubject<T> {

    override var value: T by atomic(initialValue)
        private set

    override fun onSubscribed(observer: Observer<T>) {
        super.onSubscribed(observer)

        observer.onNext(value)
    }

    override fun onNext(value: T) {
        this.value = value

        super.onNext(value)
    }
}
