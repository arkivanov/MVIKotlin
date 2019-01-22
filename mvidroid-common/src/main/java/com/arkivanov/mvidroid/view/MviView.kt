package com.arkivanov.mvidroid.view

import android.support.annotation.MainThread
import io.reactivex.Observable

/**
 * Interface of View, accepts View Models and produces View Events.
 * See [MviBinder][com.arkivanov.mvidroid.bind.MviBinder] to find out how to bind Components with Views.
 *
 * @param ViewModel type of View Model, typically a data class
 * @param ViewEvent type of View Events
 */
interface MviView<in ViewModel : Any, out ViewEvent : Any> {

    /**
     * An observable of View Events, emissions must be performed only on Main thread
     */
    val events: Observable<out ViewEvent>

    /**
     * Called when a new View Model is available, called on Main thread
     *
     * @param model a View Model
     */
    @MainThread
    fun bind(model: ViewModel)

    /**
     * Called by [MviBinder][com.arkivanov.mvidroid.bind.MviBinder] at the end of View's life-cycle, on Main thread
     */
    @MainThread
    fun onDestroy()
}
