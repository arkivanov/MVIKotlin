package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import kotlin.js.JsName

/**
 * `Store` is a place for business logic, it consumes `Intents` and produces `States`.
 * It also can produce `Labels` as side effects.
 *
 * There are three main components of the `Store`: [Bootstrapper], [Executor] and [Reducer].
 * [Store] implementations usually accept these components and manage the communication between them.
 *
 * Every [Store] should be initialized first, so it will trigger the [Bootstrapper] and will start listening for `Intents`.
 * To initialize a [Store], call its [Store.init] method. The [Store] can be instantiated on any thread
 * but most of its methods can only be called on the main thread, all such methods are annotated with @[MainThread].
 *
 * Every [Store] should be disposed at the end of lifecycle.
 * When a [Store] is disposed, all running asynchronous operations are cancelled, `State` and `Label` streams are completed.
 * To dispose a [Store], call its [Store.dispose] method.
 *
 * The [Bootstrapper] is called during initialization of the [Store].
 * All the `Actions` dispatched by the [Bootstrapper] are passed to the [Executor].
 * Please note that the [Bootstrapper] is stateful and so can not be `object` (singleton).
 *
 * The [Executor] is the main component of the [Store]. It accepts `Intents` and `Actions` and produces `Messages` and `Labels`.
 * `Messages` are passed to the [Reducer]. `Labels` are just emitted from the `Store` as side effects.
 * Please note that the [Executor] is stateful and so can not be `object` (singleton).
 *
 * The [Reducer] accepts the current `State` and a `Message` and transforms it to a new `State`.
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
 *             bootstrapper = BootstrapperImpl(), // Use SimpleBootstrapper to just immediately emit some Actions
 *             executorFactory = ::ExecutorImpl,
 *             reducer = ReducerImpl
 *         ) {
 *         }
 *
 *     private sealed class Action {
 *         // Action entries
 *     }
 *
 *     private sealed class Message {
 *         // Message entries
 *     }
 *
 *     private class BootstrapperImpl: /* Extend either ReaktiveBootstrapper or CoroutineBootstrapper */ {
 *         // Implementation here
 *     }
 *
 *     private class ExecutorImpl: /* Extend either ReaktiveExecutor or CoroutineExecutor */ {
 *         // Implementation here
 *     }
 *
 *     private object ReducerImpl : Reducer<State, Message> {
 *         override fun State.reduce(msg: Message): State =
 *             when (msg) {
 *                 // Handle all possible messages here and return a new State
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
    val isDisposed: Boolean

    /**
     * Subscribes the provided [Observer] of `States`.
     * Can be called on any thread.
     * The first emission with the current `State` will be performed synchronously on subscription, on the calling thread.
     * Further emissions are always performed on the main thread.
     *
     * @param observer an [Observer] that will receive the `States`
     */
    @JsName("states")
    fun states(observer: Observer<State>): Disposable

    /**
     * Subscribes the provided [Observer] of `Labels`.
     * Can be called on any thread.
     * Emissions are always performed on the main thread.
     *
     * @param observer an [Observer] that will receive the `Labels`
     */
    @JsName("labels")
    fun labels(observer: Observer<Label>): Disposable

    /**
     * Accepts `Intents` and passes them to the [Executor].
     * Must be called only on the main thread.
     * Does nothing if the [Store] is not yet initialized (see [init] for more information).
     *
     * @param intent an `Intent`
     */
    @JsName("accept")
    @MainThread
    fun accept(intent: Intent)

    /**
     * Initializes the [Store] and calls its [Bootstrapper] if applicable.
     * Must be called only on the main thread.
     * The behaviour is undefined if the [Store] is already disposed.
     */
    @MainThread
    fun init()

    /**
     * Disposes the [Store] and all its components.
     * Must be called only on the main thread.
     */
    @MainThread
    fun dispose()
}
