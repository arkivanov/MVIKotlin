package com.arkivanov.mvidroid.view

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface MviViewBinder<M : Any> {

    fun subscribe(models: Observable<M>): Disposable
}
