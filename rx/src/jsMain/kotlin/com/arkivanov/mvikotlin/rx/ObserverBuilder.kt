package com.arkivanov.mvikotlin.rx

actual fun <T> observer(onComplete: () -> Unit, onNext: (T) -> Unit): Observer<T> =
    ObserverImpl(onComplete, onNext)

private class ObserverImpl<T>(
    private val onComplete: () -> Unit,
    private val onNext: (T) -> Unit
) : Observer<T> {
    override fun onNext(value: T) {
        onNext.invoke(value)
    }

    override fun onComplete() {
        onComplete.invoke()
    }
}
