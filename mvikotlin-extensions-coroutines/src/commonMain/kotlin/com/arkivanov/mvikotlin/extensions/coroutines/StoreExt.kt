package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Creates and returns a [Flow] that emits [Store] states.
 *
 * Please note that the actual collection of the [Flow] may not be synchronous depending on [CoroutineContext] being used.
 */
val <State : Any> Store<*, State, *>.states: Flow<State>
    get() = toFlow(Store<*, State, *>::states)

/**
 * Creates and returns a [StateFlow] that emits [Store] states. The returned [StateFlow] is hot,
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
 * Creates and returns a [StateFlow] that emits [Store] states. The returned [StateFlow] is hot,
 * sharing the most recently emitted state from a single subscription to the [Store]
 * with multiple downstream subscribers.
 *
 * Please note that the actual collection of the [StateFlow] may not be synchronous
 * depending on [CoroutineContext] being used.
 *
 * @param lifecycle a [Lifecycle] used for cancelling the underlying [MutableStateFlow].
 */
fun <State : Any> Store<*, State, *>.stateFlow(lifecycle: Lifecycle): StateFlow<State> {
    val stateFlow = MutableStateFlow(state)
    val disposable = states(observer(onNext = { stateFlow.value = it }))
    lifecycle.doOnDestroy { disposable.dispose() }

    return stateFlow
}

/**
 * Creates and returns a [StateFlow] that emits [Store] states.
 *
 * This API is experimental because [StateFlow] interface is not stable for inheritance in 3rd party libraries.
 * Please mind binary compatibility when using this API.
 *
 * Please note that the actual collection of the [StateFlow] may not be synchronous depending on [CoroutineContext] being used.
 */
@ExperimentalCoroutinesApi
val <State : Any> Store<*, State, *>.stateFlow: StateFlow<State>
    get() = StoreStateFlow(store = this)

@Suppress("UnnecessaryOptInAnnotation")
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class StoreStateFlow<out State : Any>(
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
 * Creates and returns a [Flow] that emits [Store] labels.
 *
 * Please note that the actual collection of the [Flow] may not be synchronous depending on [CoroutineContext] being used.
 */
val <Label : Any> Store<*, *, Label>.labels: Flow<Label>
    get() = toFlow(Store<*, *, Label>::labels)

/**
 * Returns a [ReceiveChannel] that emits [Store] labels. Unlike [labels] that returns a [Flow], this API
 * is useful when labels must not be skipped while there is no subscriber. Please keep in mind that labels
 * still may be skipped if they are dispatched synchronously on [Store] initialization. If that's the case,
 * you can disable the automatic initialization by passing `autoInit = false` parameter when creating a [Store],
 * see [StoreFactory.create][com.arkivanov.mvikotlin.core.store.StoreFactory.create] for more information.
 *
 * Due to the nature of how channels work, it is recommended to have one [Channel] per subscriber.
 *
 * Please note that the actual collection of the [ReceiveChannel] may not be synchronous depending on
 * [CoroutineContext] being used.
 *
 * @param scope a [CoroutineScope] used for cancelling the underlying [Channel].
 * @param capacity a capacity of the underlying [Channel], default value is [Channel.BUFFERED].
 */
fun <Label : Any> Store<*, *, Label>.labelsChannel(
    scope: CoroutineScope,
    capacity: Int = Channel.BUFFERED,
): ReceiveChannel<Label> {
    val channel = Channel<Label>(capacity = capacity)
    val disposable = labels(observer(onNext = channel::trySend))

    scope.launch {
        try {
            awaitCancellation()
        } finally {
            disposable.dispose()
            channel.cancel()
        }
    }

    return channel
}

/**
 * Returns a [ReceiveChannel] that emits [Store] labels. Unlike [labels] that returns a [Flow], this API
 * is useful when labels must not be skipped while there is no subscriber. Please keep in mind that labels
 * still may be skipped if they are dispatched synchronously on [Store] initialization. If that's the case,
 * you can disable the automatic initialization by passing `autoInit = false` parameter when creating a [Store],
 * see [StoreFactory.create][com.arkivanov.mvikotlin.core.store.StoreFactory.create] for more information.
 *
 * Due to the nature of how channels work, it is recommended to have one [Channel] per subscriber.
 *
 * Please note that the actual collection of the [ReceiveChannel] may not be synchronous depending on
 * [CoroutineContext] being used.
 *
 * @param lifecycle a [Lifecycle] used for cancelling the underlying [Channel].
 * @param capacity a capacity of the underlying [Channel], default value is [Channel.BUFFERED].
 */
fun <Label : Any> Store<*, *, Label>.labelsChannel(
    lifecycle: Lifecycle,
    capacity: Int = Channel.BUFFERED,
): ReceiveChannel<Label> {
    val channel = Channel<Label>(capacity = capacity)
    val disposable = labels(observer(onNext = channel::trySend))

    lifecycle.doOnDestroy {
        disposable.dispose()
        channel.cancel()
    }

    return channel
}
