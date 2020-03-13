package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.badoo.reaktive.utils.ensureNeverFrozen

/**
 * Abstract implementation of the [MviView] that provides ability to dispatch `View Events`
 */
abstract class AbstractMviView<in Model : Any, Event : Any> : MviView<Model, Event> {

    init {
        ensureNeverFrozen()
    }

    private val subject = PublishSubject<Event>()

    override fun events(observer: Observer<Event>): Disposable = subject.subscribe(observer)

    /**
     * Dispatches the provided `View Event` to all subscribers
     *
     * @param event a `View Event` to be dispatched
     */
    @MainThread
    protected open fun dispatch(event: Event) {
        subject.onNext(event)
    }
}
