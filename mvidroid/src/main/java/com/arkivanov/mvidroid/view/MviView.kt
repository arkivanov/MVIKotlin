package com.arkivanov.mvidroid.view

import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Interface of View, accepts View Models and produces UI Events.
 * See [bind][com.arkivanov.mvidroid.bind.bind] to find out how to bind Components to Views.
 *
 * @param ViewModel type of View Model, typically a data class
 * @param UiEvent type of UI Events
 */
interface MviView<ViewModel : Any, UiEvent : Any> {

    /**
     * An observable of View's UI Events, emissions must be performed only on Main thread
     */
    val uiEvents: Observable<UiEvent>

    /**
     * Called when View rendering should be started (typically from onStart() callback).
     * Implement this method by subscribing to the provided observable, update UI on View Model emissions,
     * return a disposable so it can be disposed later when View rendering should be stopped (typically from onStop() callback).
     *
     * @return disposable that will be disposed when View rendering should be stopped
     */
    @MainThread
    fun subscribe(models: Observable<ViewModel>): Disposable
}
