package com.arkivanov.mvidroid.view

import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * Base class for [MviView] implementation.
 * Accepts View Models and provides ability to diff View Models field by field to avoid unnecessary view bindings.
 * Provides View Events as output and a method to dispatch them.
 */
open class MviBaseView<ViewModel : Any, ViewEvent : Any> @MainThread constructor() : MviView<ViewModel, ViewEvent> {

    private val diffs = LinkedList<Diff<*>>()
    private var oldValue: ViewModel? = null
    private val viewEventsSubject = PublishSubject.create<ViewEvent>()
    override val events: Observable<ViewEvent> = viewEventsSubject

    @CallSuper
    override fun bind(model: ViewModel) {
        val prevModel = oldValue
        oldValue = model
        diffs.forEach { it(model, prevModel) }
    }

    override fun onDestroy() {
        viewEventsSubject.onComplete()
    }

    /**
     * Generic method to register diff strategies.
     * Registered strategies are used to diff field by field every incoming View Model.
     *
     * @param mapper a function that returns a value from View Model to diff
     * @param comparator custom comparator that compares two values and returns true if they are equal, false otherwise
     * @param consumer a function that accepts value and binds it to a view
     * @param T type of value
     */
    @MainThread
    protected fun <T> registerDiff(
        mapper: (ViewModel) -> T,
        comparator: (newValue: T, oldValue: T) -> Boolean,
        consumer: (T) -> Unit
    ) {
        diffs.add(Diff(mapper, comparator, consumer))
    }

    /**
     * Registers a diff strategy that compares values by equals. See [registerDiff] for more information.
     */
    @MainThread
    protected fun <T> registerDiffByEquals(mapper: ViewModel.() -> T, consumer: (T) -> Unit) {
        registerDiff(mapper, { newValue, oldValue -> newValue == oldValue }, consumer)
    }

    /**
     * Registers a diff strategy that compares values by reference. See [registerDiff] for more information.
     */
    @MainThread
    protected fun <T> registerDiffByReference(mapper: ViewModel.() -> T, consumer: (T) -> Unit) {
        registerDiff(mapper, { newValue, oldValue -> newValue === oldValue }, consumer)
    }

    /**
     * Dispatches View Events to Component
     */
    @MainThread
    protected fun dispatch(event: ViewEvent) {
        viewEventsSubject.onNext(event)
    }

    private inner class Diff<T>(
        private val mapper: (ViewModel) -> T,
        private val comparator: (newValue: T, oldValue: T) -> Boolean,
        private val consumer: (T) -> Unit
    ) {
        operator fun invoke(newModel: ViewModel, previousModel: ViewModel?) {
            val newValue = mapper(newModel)
            if ((previousModel == null) || !comparator(newValue, mapper(previousModel))) {
                consumer(newValue)
            }
        }
    }
}
