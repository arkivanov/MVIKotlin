package com.arkivanov.mvikotlin.core.view

import com.arkivanov.mvikotlin.core.annotations.MainThread
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.internal.PublishSubject
import com.arkivanov.mvikotlin.utils.internal.ensureNeverFrozen
import kotlin.js.JsName

/**
 * Abstract implementation of the [MviView] that provides ability to dispatch `View Events`
 */
open class BaseMviView<in Model : Any, Event : Any> : MviView<Model, Event> {

    init {
        ensureNeverFrozen()
    }

    protected open val renderer: ViewRenderer<Model>? = null
    private val subject = PublishSubject<Event>()

    override fun events(observer: Observer<Event>): Disposable = subject.subscribe(observer)

    /**
     * Dispatches the provided `View Event` to all subscribers
     *
     * @param event a `View Event` to be dispatched
     */
    @JsName("dispatch")
    @MainThread
    fun dispatch(event: Event) {
        subject.onNext(event)
    }

    override fun render(model: Model) {
        renderer?.render(model)
    }
}
