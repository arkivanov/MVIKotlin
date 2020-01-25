package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.view.View
import com.badoo.reaktive.observable.Observable

val <Event> View<*, Event>.events: Observable<Event> get() = toObservable(View<*, Event>::events)
