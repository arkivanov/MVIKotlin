package com.arkivanov.mvidroid.utils

import io.reactivex.Maybe
import io.reactivex.Observable

internal fun <T : Any, R : Any> Observable<out T>.mapNullable(transformer: (T) -> R?): Observable<R> =
    flatMapMaybe { transformer(it)?.let { Maybe.just(it) } ?: Maybe.empty() }
