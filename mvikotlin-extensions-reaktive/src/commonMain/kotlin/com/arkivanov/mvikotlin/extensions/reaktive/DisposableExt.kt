package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.rx.Disposable
import com.badoo.reaktive.disposable.Disposable as ReaktiveDisposable

internal fun Disposable.toReaktiveDisposable(): ReaktiveDisposable =
    object : ReaktiveDisposable {
        override val isDisposed: Boolean
            get() = this@toReaktiveDisposable.isDisposed

        override fun dispose() {
            this@toReaktiveDisposable.dispose()
        }
    }
