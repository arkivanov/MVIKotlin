package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Store
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.observable.Observable

/**
 * Returns an [Observable] that emits [Store] `States`.
 * The first emission with the current `State` will be performed synchronously on subscription.
 * Emissions are performed on the main thread.
 */
@EventsOnMainScheduler
val <State : Any> Store<*, State, *>.states: Observable<State>
    get() = toObservable(Store<*, State, *>::states)

/**
 * Returns an [Observable] that emits [Store] `Labels`.
 * Emissions are performed on the main thread.
 */
@EventsOnMainScheduler
val <Label : Any> Store<*, *, Label>.labels: Observable<Label>
    get() = toObservable(Store<*, *, Label>::labels)
