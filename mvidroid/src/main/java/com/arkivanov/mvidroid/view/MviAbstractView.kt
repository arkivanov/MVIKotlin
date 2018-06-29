package com.arkivanov.mvidroid.view

import android.support.annotation.MainThread
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

/**
 * Abstract implementation of [MviView] that provides UI Events as output and a method to dispatch them
 */
abstract class MviAbstractView<ViewModel : Any, UiEvent : Any> @MainThread constructor() : MviView<ViewModel, UiEvent> {

    private val uiEventsRelay = PublishRelay.create<UiEvent>()
    override val uiEvents: Observable<UiEvent> = uiEventsRelay

    /**
     * Dispatches UI Events to Component
     */
    @MainThread
    protected fun dispatch(event: UiEvent) {
        uiEventsRelay.accept(event)
    }
}
