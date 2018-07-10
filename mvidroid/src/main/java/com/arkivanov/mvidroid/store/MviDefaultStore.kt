package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

internal class MviDefaultStore<State : Any, in Intent : Any, Action : Any, out Result : Any, Label : Any>
@MainThread constructor(
    initialState: State,
    bootstrapper: MviBootstrapper<Action>? = null,
    private val intentToAction: (Intent) -> Action,
    private val executor: MviExecutor<State, Action, Result, Label>,
    reducer: MviReducer<State, Result>
) : MviStore<State, Intent, Label> {

    private val stateRelay = BehaviorRelay.createDefault(initialState)
    private val labelRelay = PublishRelay.create<Label>()
    private val disposables = Disposables()

    override val state: State
        get() {
            assertOnMainThread()
            return stateRelay.value
        }

    override val states: Observable<State> = stateRelay
    override val labels: Observable<Label> = labelRelay

    init {
        assertOnMainThread()

        executor.init(
            ::state,
            {
                assertOnMainThread()
                with(reducer) {
                    stateRelay.accept(stateRelay.value.reduce(it))
                }
            },
            {
                assertOnMainThread()
                labelRelay.accept(it)
            }
        )

        bootstrapper?.bootstrap(::executeAction)?.also(disposables::add)
    }

    override fun invoke(intent: Intent) {
        assertOnMainThread()
        executeAction(intentToAction(intent))
    }

    private fun executeAction(action: Action) {
        executor(action)?.let(disposables::add)
    }

    override fun dispose() {
        assertOnMainThread()
        disposables.dispose()
    }

    override fun isDisposed(): Boolean {
        assertOnMainThread()
        return disposables.isDisposed
    }
}
