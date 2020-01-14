package com.arkivanov.mvikotlin.core.main.view

import com.arkivanov.mvikotlin.core.internal.AtomicObservers
import com.arkivanov.mvikotlin.core.internal.onNext
import com.arkivanov.mvikotlin.core.internal.register
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.view.View

abstract class AbstractView<in Model, Event> : View<Model, Event> {

    private val observers = AtomicObservers<Event>()

    override fun events(observer: Observer<Event>): Disposable = observers.register(observer)

    protected open fun dispatch(event: Event) {
        observers.onNext(event)
    }
}
