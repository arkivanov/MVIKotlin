package com.arkivanov.mvidroid.view

import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.utils.ModelDiff
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Base class for [MviView] implementation
 */
open class MviBaseView<ViewModel : Any, ViewEvent : Any> @MainThread constructor() : MviView<ViewModel, ViewEvent> {

    /**
     * See [ModelDiff] for more information
     */
    protected val diff = ModelDiff<ViewModel>()

    private val viewEventsSubject = PublishSubject.create<ViewEvent>()
    override val events: Observable<ViewEvent> = viewEventsSubject

    @CallSuper
    override fun bind(model: ViewModel) {
        diff.accept(model)
    }

    /**
     * Dispatches View Events to Component
     */
    @MainThread
    protected fun dispatch(event: ViewEvent) {
        viewEventsSubject.onNext(event)
    }
}
