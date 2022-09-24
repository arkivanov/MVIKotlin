package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Store
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.behavior.BehaviorObservable

/**
 * Returns a [BehaviorObservable] that emits [Store] states.
 * The first emission with the current `State` will be performed synchronously on subscription.
 * Emissions are performed on the main thread.
 */
@EventsOnMainScheduler
val <State : Any> Store<*, State, *>.states: BehaviorObservable<State>
    get() =
        object : BehaviorObservable<State>, Observable<State> by toObservable(Store<*, State, *>::states) {
            override val value: State get() = state
        }

/**
 * Returns an [Observable] that emits [Store] labels.
 * Emissions are performed on the main thread.
 */
@EventsOnMainScheduler
val <Label : Any> Store<*, *, Label>.labels: Observable<Label>
    get() = toObservable(Store<*, *, Label>::labels)
