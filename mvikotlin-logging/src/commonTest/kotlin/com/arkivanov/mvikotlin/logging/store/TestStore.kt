package com.arkivanov.mvikotlin.logging.store

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue

internal class TestStore<in Intent : Any, Action : Any, State : Any, in Result : Any, Label : Any>(
    initialState: State,
    private val bootstrapper: Bootstrapper<Action>?,
    executorFactory: () -> Executor<Intent, Action, State, Result, Label>,
    private val reducer: Reducer<State, Result>
) : Store<Intent, State, Label> {

    override var state: State by atomic(initialState)
        private set

    override val isDisposed: Boolean = false
    private val executor = executorFactory()

    init {
        freeze()
    }

    override fun init() {
        executor.init(
            object : Executor.Callbacks<State, Result, Label> {
                override fun onResult(result: Result) {
                    this@TestStore.state = reducer.run { state.reduce(result) }
                }

                override fun onLabel(label: Label) {
                    // no-op
                }
            }
        )

        bootstrapper?.init {
            executor.executeAction(action = it, getState = { state })
        }

        bootstrapper?.invoke()
    }

    override fun states(observer: Observer<State>): Disposable = error("Not required")

    override fun labels(observer: Observer<Label>): Disposable = error("Not required")

    override fun accept(intent: Intent) {
        executor.executeIntent(intent = intent, getState = { state })
    }

    override fun dispose() {
        // no-op
    }
}
