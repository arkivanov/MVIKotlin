package com.arkivanov.mvikotlin.rx

// Expect/actual workaround for https://github.com/arkivanov/MVIKotlin/issues/145
expect fun <T> observer(onComplete: () -> Unit = {}, onNext: (T) -> Unit = {}): Observer<T>
