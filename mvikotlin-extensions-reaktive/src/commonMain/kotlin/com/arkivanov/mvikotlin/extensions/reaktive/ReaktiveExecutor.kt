package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.core.utils.internal.atomic
import com.arkivanov.mvikotlin.core.utils.internal.initialize
import com.arkivanov.mvikotlin.core.utils.internal.requireValue
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

/**
 * An abstract implementation of the [Executor] that provides interoperability with Reaktive.
 *
 * Implements [DisposableScope] which disposes when the [Executor] is disposed.
 */
open class ReaktiveExecutor<in Intent : Any, Action : Any, in State : Any, Message : Any, Label : Any> :
    Executor<Intent, Action, State, Message, Label>,
    DisposableScope {

    private val callbacks = atomic<Executor.Callbacks<State, Message, Action, Label>>()
    private val getState: () -> State = { callbacks.requireValue().state }
    private val scope = DisposableScope()

    final override fun init(callbacks: Executor.Callbacks<State, Message, Action, Label>) {
        this.callbacks.initialize(callbacks)
    }

    final override fun executeIntent(intent: Intent) {
        executeIntent(intent, getState)
    }

    /**
     * Called by the [Store] for every received [Intent].
     *
     * Called on the main thread.
     *
     * @param intent an [Intent] received by the [Store].
     *  @param getState a [State] supplier that returns the *current* [State] of the [Store].
     */
    @MainThread
    protected open fun executeIntent(intent: Intent, getState: () -> State) {
    }

    final override fun executeAction(action: Action) {
        executeAction(action, getState)
    }

    /**
     * Called by the [Store] for every [Action] produced by the [Bootstrapper].
     *
     * Called on the main thread.
     *
     * @param action an [Action] received by the [Store] from the [Bootstrapper] or from the [Executor] itself.
     * @param getState a [State] supplier that returns the *current* [State] of the [Store].
     */
    @MainThread
    protected open fun executeAction(action: Action, getState: () -> State) {
    }

    override fun dispose() {
        scope.dispose()
    }

    /**
     * Dispatches the provided [Message] to the [Reducer].
     * The updated [State] will be available immediately after this method returns.
     *
     * Must be called on the main thread.
     *
     * @param message a [Message] to be dispatched to the [Reducer].
     */
    @MainThread
    protected fun dispatch(message: Message) {
        callbacks.requireValue().onMessage(message)
    }

    /**
     * Sends the provided [action] to the [Store] and then forwards the [action] back to the [Executor].
     * This is the recommended way of executing actions from the [Executor], as it allows
     * any wrapping Stores to also handle those actions (e.g. logging or time-traveling).
     *
     * Must be called on the main thread.
     *
     * @param action an [Action] to be forwarded back to the [Executor] via [Store].
     */
    @ExperimentalMviKotlinApi
    @MainThread
    protected fun forward(action: Action) {
        callbacks.requireValue().onAction(action)
    }

    /**
     * Sends the provided [Label] to the [Store] for publication.
     *
     * Must be called on the main thread.
     *
     * @param label a [Label] to be published.
     */
    @MainThread
    protected fun publish(label: Label) {
        callbacks.requireValue().onLabel(label)
    }

    /*
    * DisposableScope delegate
    */

    final override val isDisposed: Boolean get() = scope.isDisposed

    final override fun <T : Disposable> T.scope(): T = scope.run { this@scope.scope() }

    final override fun <T> T.scope(onDispose: (T) -> Unit): T = scope.run { this@scope.scope(onDispose) }


    final override fun Completable.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                onSubscribe = onSubscribe,
                onError = onError,
                onComplete = onComplete
            )
        }

    final override fun <T> Maybe<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                onSubscribe = onSubscribe,
                onError = onError,
                onComplete = onComplete,
                onSuccess = onSuccess
            )
        }

    final override fun <T> Observable<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onComplete: (() -> Unit)?,
        onNext: ((T) -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                onSubscribe = onSubscribe,
                onError = onError,
                onComplete = onComplete,
                onNext = onNext
            )
        }

    final override fun <T> Single<T>.subscribeScoped(
        onSubscribe: ((Disposable) -> Unit)?,
        onError: ((Throwable) -> Unit)?,
        onSuccess: ((T) -> Unit)?
    ): Disposable =
        scope.run {
            this@subscribeScoped.subscribeScoped(
                onSubscribe = onSubscribe,
                onError = onError,
                onSuccess = onSuccess
            )
        }
}
