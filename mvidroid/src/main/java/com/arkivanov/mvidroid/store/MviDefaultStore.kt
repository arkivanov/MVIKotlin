package com.arkivanov.mvidroid.store

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.components.MviAction
import com.arkivanov.mvidroid.components.MviBootstrapper
import com.arkivanov.mvidroid.components.MviIntentToAction
import com.arkivanov.mvidroid.components.MviReducer
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

internal class MviDefaultStore<State : Any, in Intent : Any, out Result : Any, Label : Any, Action : MviAction<State, Result, Label>>
@MainThread constructor(
    initialState: State,
    bootstrapper: MviBootstrapper<Action>? = null,
    private val intentToAction: MviIntentToAction<Intent, Action>,
    reducer: MviReducer<State, Result>
) : MviStore<State, Intent, Label> {

    private val stateRelay = BehaviorRelay.createDefault(initialState)
    private val labelRelay = PublishRelay.create<Label>()
    private val disposables = Disposables()

    override val state: State
        get() = assertOnMainThread().let { stateRelay.value }

    override val states: Observable<State> = stateRelay
    override val labels: Observable<Label> = labelRelay

    private val getState = ::state

    private val dispatch = { result: Result ->
        assertOnMainThread()
        with(reducer) {
            stateRelay.accept(stateRelay.value.reduce(result))
        }
    }

    private val publish = { label: Label ->
        assertOnMainThread()
        labelRelay.accept(label)
    }

    init {
        assertOnMainThread()
        bootstrapper?.bootstrap(::processAction)?.also(disposables::add)
    }

    final override fun invoke(intent: Intent) {
        assertOnMainThread()
        processAction(intentToAction.select(intent))
    }

    private fun processAction(action: Action) {
        action(getState, dispatch, publish)?.also(disposables::add)
    }

    override fun dispose() {
        assertOnMainThread()
        disposables.dispose()
    }

    override fun isDisposed(): Boolean = assertOnMainThread().let { disposables.isDisposed }
}
