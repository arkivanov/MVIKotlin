package com.arkivanov.mvidroid.store.factory.defaultstore

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

internal class MviDefaultStore<State : Any, in Intent : Any, Action : Any, out Result : Any, Label : Any> @MainThread constructor(
    initialState: State,
    bootstrapper: MviBootstrapper<Action>? = null,
    private val intentToAction: (Intent) -> Action,
    private val executor: MviExecutor<State, Action, Result, Label>,
    reducer: MviReducer<State, Result>
) : MviStore<State, Intent, Label> {

    private val statesSubject = BehaviorSubject.createDefault(initialState)
    private val labelsSubject = BehaviorSubject.create<Label>()
    override val states: Observable<State> = statesSubject
    override val labels: Observable<Label> = labelsSubject

    override val state: State
        get() {
            assertOnMainThread()
            return statesSubject.value
        }

    private val disposables = Disposables()

    init {
        assertOnMainThread()

        executor.init(
            ::state,
            {
                assertOnMainThread()
                with(reducer) {
                    statesSubject.onNext(statesSubject.value.reduce(it))
                }
            },
            {
                assertOnMainThread()
                labelsSubject.onNext(it)
            }
        )

        bootstrapper?.bootstrap(::executeAction)?.also(disposables::add)
    }

    override fun invoke(intent: Intent) {
        assertOnMainThread()
        executeAction(intentToAction(intent))
    }

    private fun executeAction(action: Action) {
        executor.execute(action)?.let(disposables::add)
    }

    override fun dispose() {
        assertOnMainThread()
        disposables.dispose()
        statesSubject.onComplete()
        labelsSubject.onComplete()
    }

    override fun isDisposed(): Boolean {
        assertOnMainThread()
        return disposables.isDisposed
    }
}