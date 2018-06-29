package com.arkivanov.mvidroid.utils

import com.arkivanov.kfunction.KFunction
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableSource

/**
 * Returns an Observable that applies a specified function to each item emitted by the source ObservableSource and
 * emits only non-null results of these function applications.
 *
 * @param mapper a function to apply to each item emitted by the ObservableSource
 * @param T input type
 * @param R output type
 * @return an Observable that emits only non-null items transformed by the specified function from the source ObservableSource
 */
inline fun <T : Any, R : Any> ObservableSource<out T>.mapNotNull(crossinline mapper: KFunction<T, R?>): Observable<R> =
    Observable.wrap(this).flatMapMaybe { mapper(it)?.let { Maybe.just(it) } ?: Maybe.empty() }
