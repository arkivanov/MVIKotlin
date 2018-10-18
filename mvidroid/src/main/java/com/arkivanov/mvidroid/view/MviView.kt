package com.arkivanov.mvidroid.view

import android.support.annotation.MainThread
import io.reactivex.Observable

/**
 * Interface of View, accepts View Models and produces UI Events.
 * See [Binder][com.arkivanov.mvidroid.bind.Binder] to find out how to bind Components with Views.
 *
 * @param ViewModel type of View Model, typically a data class
 * @param UiEvent type of UI Events
 */
interface MviView<in ViewModel : Any, UiEvent : Any> {

    /**
     * An observable of View's UI Events, emissions must be performed only on Main thread
     */
    val uiEvents: Observable<UiEvent>

    /**
     * Called when a new View Model is available, called on Main thread
     *
     * @param model a View Model
     */
    @MainThread
    fun bind(model: ViewModel)

    /**
     * Called by [Binder][com.arkivanov.mvidroid.bind.Binder] at the end of View's life-cycle
     */
    @MainThread
    fun onDestroy()
}
