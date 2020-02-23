package com.arkivanov.mvikotlin.main.view

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.core.internal.rx.Subject
import com.arkivanov.mvikotlin.core.internal.rx.onNext
import com.arkivanov.mvikotlin.core.internal.rx.subscribe
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.view.View

abstract class AbstractView<in Model : Any, Event : Any> : View<Model, Event> {

    private val subject = Subject<Event>()

    override fun events(observer: Observer<Event>): Disposable = subject.subscribe(observer)

    @MainThread
    protected open fun dispatch(event: Event) {
        subject.onNext(event)
    }
}
