package com.arkivanov.mvikotlin.rx

inline fun <T> observer(
    crossinline onComplete: () -> Unit = {},
    crossinline onNext: (T) -> Unit = {}
): Observer<T> {
    // Fixes weird "ObjectLiteral is not defined" error (https://github.com/arkivanov/MVIKotlin/issues/145)
    @Suppress("UnnecessaryVariable")
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
