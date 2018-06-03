package com.arkivanov.mvidroid.store

import com.arkivanov.mvidroid.components.MviBootstrapper
import com.arkivanov.mvidroid.components.MviIntentToAction
import com.arkivanov.mvidroid.components.MviReducer
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Default implementation of Store, see [MviStore] for more information and implementation example.
 * Must be created only on Main thread.
 *
 * @param initialState initial State
 * @param bootstrapper optional Bootstrapper that will initialize the Store, see [MviBootstrapper]
 * @param intentToAction maps Intents to Actions, see [MviAction]
 * @param reducer Reducer that will reduce States based on Results, see [MviReducer]
 * @param S type of State
 * @param I type of Intents
 * @param R type of Results
 * @param L type of Labels
 */
open class MviDefaultStore<S : Any, in I : Any, out R : Any, L : Any, A : MviAction<S, R, L>>(
    initialState: S,
    bootstrapper: MviBootstrapper<A>? = null,
    private val intentToAction: MviIntentToAction<I, A>,
    reducer: MviReducer<S, R>
) : MviStore<S, I, L> {

    private val stateSubject = BehaviorRelay.createDefault(initialState)
    private val labelSubject = PublishSubject.create<L>()
    private val disposables = Disposables()

    override val state: S
        get() = assertOnMainThread().let { stateSubject.value }

    override val states: Observable<S> = stateSubject
    override val labels: Observable<L> = labelSubject

    private val getState = ::state

    private val dispatch = { result: R ->
        assertOnMainThread()
        with(reducer) {
            stateSubject.accept(stateSubject.value.reduce(result))
        }
    }

    private val publish = { label: L ->
        assertOnMainThread()
        labelSubject.onNext(label)
    }

    init {
        assertOnMainThread()
        bootstrapper?.bootstrap(::processAction)?.also(disposables::add)
    }

    final override fun invoke(intent: I) {
        assertOnMainThread()
        processAction(intentToAction.select(intent))
    }

    private fun processAction(action: A) {
        action(getState, dispatch, publish)?.also(disposables::add)
    }

    override fun dispose() {
        assertOnMainThread()
        disposables.dispose()
    }

    override fun isDisposed(): Boolean = assertOnMainThread().let { disposables.isDisposed }
}
