package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Store
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.observable.Observable

@EventsOnMainScheduler
val <State : Any> Store<*, State, *>.states: Observable<State>
    get() = toObservable(Store<*, State, *>::states)

@EventsOnMainScheduler
val <Label : Any> Store<*, *, Label>.labels: Observable<Label>
    get() = toObservable(Store<*, *, Label>::labels)
