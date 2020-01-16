package com.arkivanov.mvikotlin.core.internal.rx

import com.arkivanov.mvikotlin.core.rx.Observer

inline fun <T> observer(
    crossinline onNext: (T) -> Unit = {},
    crossinline onComplete: () -> Unit = {}
): Observer<T> =
    object : Observer<T> {
        override fun onNext(value: T) {
            onNext.invoke(value)
        }

        override fun onComplete() {
            onComplete.invoke()
        }
    }
