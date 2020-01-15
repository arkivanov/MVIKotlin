package com.arkivanov.mvikotlin.core.internal.rx

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getAndSet
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.atomic.updateAndGet

typealias Subject<T> = AtomicReference<Map<Disposable, Observer<T>>?>

@Suppress("FunctionName")
fun <T> Subject(): Subject<T> = Subject(emptyMap())

val <T> Subject<T>.isActive: Boolean get() = value != null

fun <T> Subject<T>.subscribe(observer: Observer<T>): Disposable = subscribeActual(observer)

fun <T> Subject<T>.subscribe(observer: Observer<T>, value: T): Disposable =
    subscribeActual(observer) {
        observer.onNext(value)
    }

private inline fun <T> Subject<T>.subscribeActual(observer: Observer<T>, onSubscribed: () -> Unit = {}): Disposable {
    val disposable =
        Disposable {
            update { it?.minus(this) }
        }

    val newMap = updateAndGet { it?.plus(disposable to observer) }
    if (newMap == null) {
        disposable.dispose()
        observer.onComplete()
    } else {
        onSubscribed()
    }

    return disposable
}

fun <T> Subject<T>.onNext(value: T) {
    this.value?.values?.forEach {
        it.onNext(value)
    }
}

fun <T> Subject<T>.onComplete() {
    getAndSet(null)?.forEach { (disposable, observer) ->
        disposable.dispose()
        observer.onComplete()
    }
}
