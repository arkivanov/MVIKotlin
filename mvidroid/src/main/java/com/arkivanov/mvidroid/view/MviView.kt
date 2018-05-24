package com.arkivanov.mvidroid.view

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class MviView<M : Any>(
    private val models: Observable<M>,
    private val binder: MviViewBinder<M>
) {
    private var disposable: Disposable? = null

    fun subscribe() {
        if (disposable == null) {
            disposable = binder.subscribe(models)
        }
    }

    fun unsubscribe() {
        disposable?.dispose()
        disposable = null
    }
}
