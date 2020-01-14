package com.arkivanov.mvikotlin.core.internal

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.update

typealias AtomicObservers<T> = AtomicReference<List<Observer<T>>>

fun <T> AtomicObservers(): AtomicObservers<T> = AtomicObservers(emptyList())

fun <T> AtomicObservers<T>.register(observer: Observer<T>): Disposable {
    update { it + observer }

    return Disposable { update { it - observer } }
}

fun <T> AtomicObservers<T>.register(observer: Observer<T>, value: T): Disposable =
    register(observer)
        .also { observer.onNext(value) }

fun <T> AtomicObservers<T>.onNext(value: T) {
    this.value.forEach {
        it.onNext(value)
    }
}

fun <T> AtomicObservers<T>.complete() {
    getAndSet(emptyList()).forEach(Observer<*>::onComplete)
}
