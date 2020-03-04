package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.view.ViewEvents
import com.badoo.reaktive.annotations.EventsOnMainScheduler
import com.badoo.reaktive.observable.Observable

@EventsOnMainScheduler
val <Event : Any> ViewEvents<Event>.events: Observable<Event>
    get() = toObservable(ViewEvents<Event>::events)
