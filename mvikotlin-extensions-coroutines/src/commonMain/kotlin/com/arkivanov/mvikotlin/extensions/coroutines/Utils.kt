package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
internal inline fun <T, R> T.toFlow(
    crossinline subscribe: T.(Observer<R>) -> Disposable
): Flow<R> =
    callbackFlow {
        // Fixes weird "ObjectLiteral is not defined" error (https://github.com/arkivanov/MVIKotlin/issues/145)
        val observer = observer<R>(
            onComplete = { channel.close() },
            onNext = { channel.offer(it) }
        )
        val disposable = subscribe(observer)
        awaitClose(disposable::dispose)
    }
