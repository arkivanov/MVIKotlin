package com.arkivanov.mvidroid.view

import android.support.annotation.CallSuper
import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * Base class for [MviView] implementation.
 * Accepts View Models and provides ability to diff View Models field by field to avoid unnecessary view bindings.
 * Provides UI Events as output and a method to dispatch them.
 */
open class MviBaseView<ViewModel : Any, UiEvent : Any> @MainThread constructor() : MviView<ViewModel, UiEvent> {

    private val diffs = LinkedList<Diff<*, *>>()
    private var oldValue: ViewModel? = null
    private val uiEventsSubject = PublishSubject.create<UiEvent>()
    override val uiEvents: Observable<UiEvent> = uiEventsSubject

    @CallSuper
    override fun bind(model: ViewModel) {
        val prevModel = oldValue
        oldValue = model
        diffs.forEach { it.diff(model, prevModel) }
    }

    override fun onDestroy() {
        uiEventsSubject.onComplete()
    }

    /**
     * Generic method to register diff strategies.
     * Registered strategies are used to diff field by field every incoming View Model.
     *
     * @param view a view to set value to
     * @param getValue a function that returns a value from View Model to diff
     * @param comparator custom comparator that compares two values and returns true if they are equal, false otherwise
     * @param setValue a function that accepts value and binds it to a view
     * @param V type of view
     * @param T type of value
     */
    @MainThread
    protected fun <V : Any, T> registerDiff(
        view: V,
        getValue: ViewModel.() -> T,
        comparator: (newValue: T, oldValue: T) -> Boolean,
        setValue: V.(T) -> Unit
    ) {
        diffs.add(Diff(view, getValue, comparator, setValue))
    }

    /**
     * Registers a diff strategy that compares values by equals. See [registerDiff] for more information.
     */
    @MainThread
    protected fun <V : Any, T> registerDiffByEquals(view: V, getValue: ViewModel.() -> T, setValue: V.(T) -> Unit) {
        registerDiff(view, getValue, { newValue, oldValue -> newValue == oldValue }, setValue)
    }

    /**
     * Registers a diff strategy that compares values by reference. See [registerDiff] for more information.
     */
    @MainThread
    protected fun <V : Any, T> registerDiffByReference(view: V, getValue: ViewModel.() -> T, setValue: V.(T) -> Unit) {
        registerDiff(view, getValue, { newValue, oldValue -> newValue === oldValue }, setValue)
    }

    /**
     * Dispatches UI Events to Component
     */
    @MainThread
    protected fun dispatch(event: UiEvent) {
        uiEventsSubject.onNext(event)
    }

    private inner class Diff<V : Any, T>(
        private val view: V,
        private val getValue: ViewModel.() -> T,
        private val comparator: (newValue: T, oldValue: T) -> Boolean,
        private val setValue: V.(T) -> Unit
    ) {
        fun diff(newModel: ViewModel, previousModel: ViewModel?) {
            val newValue = newModel.getValue()
            if ((previousModel == null) || !comparator(newValue, previousModel.getValue())) {
                view.setValue(newValue)
            }
        }
    }
}
