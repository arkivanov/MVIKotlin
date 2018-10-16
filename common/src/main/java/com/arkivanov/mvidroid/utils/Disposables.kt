package com.arkivanov.mvidroid.utils

import android.support.annotation.VisibleForTesting
import io.reactivex.disposables.Disposable

class Disposables : Disposable {
    private var disposables: MutableList<Disposable>? = ArrayList()

    override fun isDisposed(): Boolean = disposables == null

    override fun dispose() {
        disposables?.forEach(Disposable::dispose)
        disposables = null
    }

    fun add(disposable: Disposable) {
        disposables?.apply {
            removeAll(Disposable::isDisposed)
            add(disposable)
        }
    }

    @VisibleForTesting
    internal fun contains(disposable: Disposable): Boolean = disposables?.contains(disposable) ?: false
}
