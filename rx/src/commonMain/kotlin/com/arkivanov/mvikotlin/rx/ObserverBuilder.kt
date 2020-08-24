package com.arkivanov.mvikotlin.rx

inline fun <T> observer(
    crossinline onComplete: () -> Unit = {},
    crossinline onNext: (T) -> Unit = {}
): Observer<T> {
    val result = object : Observer<T> {
        override fun onNext(value: T) {
            onNext.invoke(value)
        }

        override fun onComplete() {
            onComplete.invoke()
        }
    }
    return result
}
