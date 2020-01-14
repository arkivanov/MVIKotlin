package com.arkivanov.mvikotlin.core.store

abstract class BaseExecutor<in Intent, in Action, State, Result, Label> : Executor<Intent, Action, State, Result, Label> {

    private var isInitialized = false
    private lateinit var stateSupplier: () -> State
    private lateinit var resultConsumer: (Result) -> Unit
    private lateinit var labelConsumer: (Label) -> Unit
    protected val state: State get() = stateSupplier()

    final override fun init(stateSupplier: () -> State, resultConsumer: (Result) -> Unit, labelConsumer: (Label) -> Unit) {
        require(!isInitialized) { "Executor is already initialized" }

        isInitialized = true
        this.stateSupplier = stateSupplier
        this.resultConsumer = resultConsumer
        this.labelConsumer = labelConsumer
    }

    override fun handleIntent(intent: Intent) {
    }

    override fun handleAction(action: Action) {
    }

    override fun dispose() {
    }

    protected fun dispatch(result: Result) {
        resultConsumer(result)
    }

    protected fun publish(label: Label) {
        labelConsumer(label)
    }
}
