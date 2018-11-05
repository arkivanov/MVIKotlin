package com.arkivanov.mvidroid.store.defaultstore

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.component.MviBootstrapper
import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

internal class MviDefaultStore<out State : Any, in Intent : Any, in Action : Any, out Result : Any, out Label : Any> @MainThread constructor(
    initialState: State,
    bootstrapper: MviBootstrapper<Action>? = null,
    private val intentToAction: (Intent) -> Action,
    private val executor: MviExecutor<State, Action, Result, Label>,
    reducer: MviReducer<State, Result>
) : MviStore<State, Intent, Label> {

    private val statesSubject = BehaviorSubject.createDefault(initialState)
    private val labelsSubject = PublishSubject.create<Label>()
    override val states: Observable<out State> = statesSubject
    override val labels: Observable<out Label> = labelsSubject

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

    override fun accept(intent: Intent) {
        executeAction(intentToAction(intent))
    }

    private fun executeAction(action: Action) {
        assertOnMainThread()
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