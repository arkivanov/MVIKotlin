package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.rx.observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext

/**
 * Returns a [Flow] that emits [Store] states.
 *
 * Please note that the actual collection of the [Flow] may not be synchronous depending on [CoroutineContext] being used.
 */
val <State : Any> Store<*, State, *>.states: Flow<State>
    get() = toFlow(Store<*, State, *>::states)

/**
 * Returns a [StateFlow] that emits [Store] states. The returned [StateFlow] is hot,
 * started in the given coroutine [scope], sharing the most recently emitted state from
 * a single subscription to the [Store] with multiple downstream subscribers.
 *
 * Please note that the actual collection of the [StateFlow] may not be synchronous depending on [CoroutineContext] being used.
 *
 * @param scope the coroutine scope in which sharing is started.
 * @param started the strategy that controls when sharing is started and stopped, default value is [SharingStarted.Eagerly].
 */
fun <State : Any> Store<*, State, *>.stateFlow(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Eagerly,
): StateFlow<State> = states.stateIn(scope, started, state)

/**
 * Returns a [StateFlow] that emits [Store] states.
 *
 * This is API is experimental because [StateFlow] interface is not stable for inheritance in 3rd party libraries.
 * Please mind binary compatibility when using this API.
 *
 * Please note that the actual collection of the [StateFlow] may not be synchronous depending on [CoroutineContext] being used.
 */
@ExperimentalCoroutinesApi
val <State : Any> Store<*, State, *>.stateFlow: StateFlow<State>
    get() = StoreStateFlow(store = this)

private class StoreStateFlow<State : Any>(
    private val store: Store<*, State, *>,
) : StateFlow<State> {

    override val value: State get() = store.state
    override val replayCache: List<State> get() = listOf(store.state)

    override suspend fun collect(collector: FlowCollector<State>): Nothing {
        val flow = MutableStateFlow(store.state)
        val disposable = store.states(observer { flow.value = it })

        try {
            flow.collect(collector)
        } finally {
            disposable.dispose()
        }
    }
}

/**
 * Returns a [Flow] that emits [Store] labels.
 *
 *  Please note that the actual collection of the [Flow] may not be synchronous depending on [CoroutineContext] being used.
 */
val <Label : Any> Store<*, *, Label>.labels: Flow<Label>
    get() = toFlow(Store<*, *, Label>::labels)
