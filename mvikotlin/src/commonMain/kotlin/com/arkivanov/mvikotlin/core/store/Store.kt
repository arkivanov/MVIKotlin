package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer

/**
 * `Store` is a place for business logic, it consumes `Intents` and produces `States`.
 * It also can produce `Labels` as side effects.
 *
 * There are three main components of the `Store`: [Bootstrapper], [Executor] and [Reducer].
 * The [Store] normally accepts these components and manages the communication between them.
 *
 * The [Bootstrapper] is normally called during instantiation of the [Store].
 * All the `Actions` dispatched by the [Bootstrapper] are passed to the [Executor].
 *
 * The [Executor] is the main component of the [Store]. It accepts `Intents` and `Actions` and produces `Results` and `Labels`.
 * `Results` are then passed to the [Reducer]. `Labels` are just emitted from the `Store` as side effects.
 *
 * The [Reducer] accepts a current `State` and a `Result` and transforms it to a new `State`.
 * The new `State` then applied to the [Store] and emitted.
 *
 * Here is the suggested implementation template of the `Store`:
 * ```kotlin
 * interface MyStore : Store<Intent, State, Label> {
 *     sealed class Intent {
 *         // Intent entries
 *     }
 *
 *     data class State()
 *
 *     sealed class Label {
 *         // News entries
 *     }
 * }
 *
 * class MyStoreFactory(
 *     private val factory: StoreFactory,
 *     // More dependencies here
 * ) {
 *
 *     fun create(): MyStore =
 *         object : MyStore, Store<Intent, State, Label> by factory.create(
 *             name = "MyStore",
 *             initialState = State(),
 *             bootstrapper = BootstrapperImpl(),
 *             executorFactory = ::ExecutorImpl,
 *             reducer = ReducerImpl
 *         ) {
 *         }
 *
 *     private sealed class Action {
 *         // Action entries
 *     }
 *
 *     private sealed class Result {
 *         // Result entries
 *     }
 *
 *     private class BootstrapperImpl: /* Extend either ReaktiveBootstrapper or SuspendBootstrapper */ {
 *         // Implementation here
 *     }
 *
 *     private class ExecutorImpl: /* Extend either ReaktiveExecutor or SuspendExecutor */ {
 *         // Implementation here
 *     }
 *
 *     private object ReducerImpl : Reducer<State, Result> {
 *         override fun State.reduce(result: Result): State =
 *             when (result) {
 *                 // Handle all possible results here and return a new State
 *             }
 *     }
 * }
 * ```
 *
 * @see Bootstrapper
 * @see Executor
 * @see Reducer
 * @see Observer
 * @see Disposable
 */
interface Store<in Intent : Any, out State : Any, out Label : Any> {

    /**
     * Returns the current `State` of the [Store]
     */
    val state: State

    /**
     * Returns whether the [Store] is disposed or not
     */
    @MainThread
    val isDisposed: Boolean

    /**
     * Subscribes the provided [Observer] of `States`.
     * The first emission with the current `State` will be performed synchronously on subscription.
     * Emissions are performed on the main thread.
     *
     * @param observer an [Observer] that will receive the `States`
     */
    @MainThread
    fun states(observer: Observer<State>): Disposable

    /**
     * Subscribes the provided [Observer] of `Labels`.
     * Emissions are performed on the main thread.
     *
     * @param observer an [Observer] that will receive the `Labels`
     */
    @MainThread
    fun labels(observer: Observer<Label>): Disposable

    /**
     * Accepts `Intents` and passes them to the [Executor]
     *
     * @param intent an `Intent`
     */
    @MainThread
    fun accept(intent: Intent)

    /**
     * Disposes the [Store] and all its components
     */
    @MainThread
    fun dispose()
}
