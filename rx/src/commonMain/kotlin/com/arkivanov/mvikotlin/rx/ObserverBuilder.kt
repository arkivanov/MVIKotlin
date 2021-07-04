package com.arkivanov.mvikotlin.rx

// Not inlined until https://youtrack.jetbrains.com/issue/KT-45866.
// Also check https://github.com/arkivanov/MVIKotlin/issues/145 before making it inlined.
fun <T> observer(onComplete: (() -> Unit)? = null, onNext: ((T) -> Unit)? = null): Observer<T> =
    ObserverImpl(onComplete, onNext)

private class ObserverImpl<in T>(
    private val onComplete: (() -> Unit)? = null,
    private val onNext: ((T) -> Unit)? = null
) : Observer<T> {
    override fun onNext(value: T) {
        onNext?.invoke(value)
    }

    override fun onComplete() {
        onComplete?.invoke()
    }
}
