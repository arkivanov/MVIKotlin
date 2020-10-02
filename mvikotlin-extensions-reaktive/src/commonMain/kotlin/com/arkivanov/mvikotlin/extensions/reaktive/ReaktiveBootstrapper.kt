package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.initialize
import com.arkivanov.mvikotlin.utils.internal.requireValue
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

/**
 * An abstract implementation of the [Bootstrapper] that provides interoperability with Reaktive.
 * Implements [DisposableScope] which disposes when the [Bootstrapper] is disposed.
 */
abstract class ReaktiveBootstrapper<Action : Any> : Bootstrapper<Action>, DisposableScope {

    private val actionConsumer = atomic<(Action) -> Unit>()
    private val scope = DisposableScope()

    final override fun init(actionConsumer: (Action) -> Unit) {
        this.actionConsumer.initialize(actionConsumer)
    }

    /**
     * Dispatches the `Action` to the [Store]
     *
     * @param action an `Action` to be dispatched
     */
    protected fun dispatch(action: Action) {
        actionConsumer.requireValue().invoke(action)
    }

    override fun dispose() {
        scope.dispose()
    }

    /*
     * DisposableScope delegate
     */

    final override val isDisposed: Boolean get() = scope.isDisposed

    final override fun <T : Disposable> T.scope(): T = scope.run { this@scope.scope() }

    final override fun <T> T.scope(onDispose: (T) -> Unit): T = scope.run { this@scope.scope(onDispose) }

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
}
