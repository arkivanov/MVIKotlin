package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.rx.observer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal inline fun <T, R> T.toFlow(
    crossinline subscribe: T.(Observer<R>) -> Disposable,
): Flow<R> =
    callbackFlow {
        val disposable =
            subscribe(
                observer(
                    onComplete = { channel.close() },
                    onNext = { channel.trySend(it) }
                )
            )

        awaitClose(disposable::dispose)
    }
