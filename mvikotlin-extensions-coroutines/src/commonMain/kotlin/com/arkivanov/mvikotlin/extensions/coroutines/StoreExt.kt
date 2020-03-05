package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * Returns a [Flow] that emits [Store] `States`.
 * The first emission with the current `State` will be performed synchronously on collection.
 * Please not that the actual collection of the [Flow] may not be synchronous depending on [CoroutineContext] being used.
 */
@ExperimentalCoroutinesApi
val <State : Any> Store<*, State, *>.states: Flow<State>
    get() = toFlow(Store<*, State, *>::states)

/**
 * Returns a [Flow] that emits [Store] `Labels`
 */
@ExperimentalCoroutinesApi
val <Label : Any> Store<*, *, Label>.labels: Flow<Label>
    get() = toFlow(Store<*, *, Label>::labels)
