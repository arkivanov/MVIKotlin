package com.arkivanov.mvidroid.store.component

import android.support.annotation.MainThread
import io.reactivex.disposables.Disposable

/**
 * Executors are used to execute Actions, dispatch Results and publish Labels.
 * In other words it's a place for business logic.
 * IMPORTANT: please pay attention that it must not be a singleton.
 */
abstract class MviExecutor<State : Any, in Action : Any, Result : Any, Label : Any> @MainThread constructor() {

    private var isInitialized: Boolean = false
    private lateinit var stateSupplier: () -> State
    private lateinit var resultConsumer: (Result) -> Unit
    private lateinit var labelConsumer: (Label) -> Unit

    /**
     * Provides current State of Store, must be accessed only on Main thread
     */
    @get:MainThread
    protected val state: State
        get() = stateSupplier()

    /**
     * Called internally by Store
     */
    @MainThread
    fun init(stateSupplier: () -> State, resultConsumer: (Result) -> Unit, labelConsumer: (Label) -> Unit) {
        if (isInitialized) {
            throw IllegalStateException("MviExecutor cannot be reused, please make sure that it is not a singleton")
        }

        isInitialized = true
        this.stateSupplier = stateSupplier
        this.resultConsumer = resultConsumer
        this.labelConsumer = labelConsumer
    }

    /**
     * Invoked by Store with Action to execute, always on Main thread.
     *
     * @param action an Action that should be executed
     * @return disposable if there are any background operations, null otherwise.
     * A returned disposable will be managed by Store and disposed at the end of life-cycle.
     */
    @MainThread
    abstract fun execute(action: Action): Disposable?

    /**
     * Dispatches Result. Any dispatched Result will be synchronously processed by Store
     * which means a new State will synchronously applied to Store and emitted.
     * You can get a new State right after this method return. Must be called only on Main thread.
     *
     * @param result a Result to dispatch
     */
    @MainThread
    protected fun dispatch(result: Result) {
        resultConsumer(result)
    }

    /**
     * Publishes Label. Any published Label will be synchronously processed and emitted by Store.
     * Must be called only on Main thread
     *
     * @param label a Label to publish
     */
    @MainThread
    protected fun publish(label: Label) {
        labelConsumer(label)
    }
}
