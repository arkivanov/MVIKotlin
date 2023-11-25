package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.rx.observer
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

internal inline fun <T, R> T.toObservable(
    crossinline subscribe: T.(Observer<R>) -> Disposable,
): Observable<R> =
    observable { emitter ->
        val disposable = subscribe(
            observer(
                onComplete = emitter::onComplete,
                onNext = emitter::onNext,
            )
        )
        emitter.setDisposable(disposable.toReaktiveDisposable())
    }
