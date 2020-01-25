package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.BaseBootstrapper
import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

@UseExperimental(ExperimentalReaktiveApi::class)
abstract class ReaktiveBootstrapper<Action> : BaseBootstrapper<Action>(), DisposableScope {

    private val scope = DisposableScope()

    override fun dispose() {
        scope.dispose()

        super.dispose()
    }

    /*
     * DisposableScope delegate
     */

    final override val isDisposed: Boolean get() = scope.isDisposed

    final override fun <T : CompleteCallback> T.scope(): T = scope.run { this@scope.scope() }

    final override fun <T : Disposable> T.scope(): T = scope.run { this@scope.scope() }

    final override fun Completable.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                isThreadLocal = isThreadLocal,
                onSubscribe = onSubscribe,
                onError = onError,
                onComplete = onComplete
            )
        }

    final override fun <T> Maybe<T>.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                isThreadLocal = isThreadLocal,
                onSubscribe = onSubscribe,
                onError = onError,
                onComplete = onComplete,
                onSuccess = onSuccess
            )
        }

    final override fun <T> Observable<T>.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onNext: ((T) -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                isThreadLocal = isThreadLocal,
                onSubscribe = onSubscribe,
                onError = onError,
                onComplete = onComplete,
                onNext = onNext
            )
        }

    final override fun <T> Single<T>.subscribeScoped(
        isThreadLocal: Boolean,
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                isThreadLocal = isThreadLocal,
                onSubscribe = onSubscribe,
                onError = onError,
                onSuccess = onSuccess
            )
        }
}
