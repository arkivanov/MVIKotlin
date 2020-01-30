package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
val <State : Any> Store<*, State, *>.states: Flow<State>
    get() = toFlow(Store<*, State, *>::states)

@ExperimentalCoroutinesApi
val <Label : Any> Store<*, *, Label>.labels: Flow<Label>
    get() = toFlow(Store<*, *, Label>::labels)
