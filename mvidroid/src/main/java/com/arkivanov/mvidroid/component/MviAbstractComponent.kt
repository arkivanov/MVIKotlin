package com.arkivanov.mvidroid.component

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.utils.assertOnMainThread
import com.arkivanov.mvidroid.utils.mapNotNull
import com.jakewharton.rxrelay2.Relay
import io.reactivex.disposables.CompositeDisposable

/**
 * Abstract implementation of [MviComponent].
 *
 * Responsibilities:
 * * Everything from [MviComponent]
 * * If Labels relay is provided, converts Labels to Stores' Intents and redirects them to appropriate Stores
 * * If Labels relay is provided, redirects Labels from non-persistent Stores to Labels relay
 */
abstract class MviAbstractComponent<in UiEvent : Any, out States : Any> @MainThread constructor(
    private val stores: List<MviStoreBundle<*, UiEvent>>,
    labels: Relay<Any>? = null
) : MviComponent<UiEvent, States> {

    private val disposables = CompositeDisposable()

    init {
        assertOnMainThread()
        labels?.also { relay ->
            stores.forEach { it.connectLabels(relay) }
        }
    }

    override fun invoke(event: UiEvent) {
        assertOnMainThread()
        stores.forEach { it.handleUiEvent(event) }
    }

    override fun dispose() {
        assertOnMainThread()
        disposables.dispose()
        stores.forEach {
            if (!it.isPersistent) {
                it.store.dispose()
            }
        }
    }

    override fun isDisposed(): Boolean {
        assertOnMainThread()

        return disposables.isDisposed
    }

    private fun <Intent : Any> MviStoreBundle<Intent, *>.connectLabels(labels: Relay<Any>) {
        labelTransformer?.also { transformer ->
            disposables.add(labels.mapNotNull(transformer).subscribe { store(it) })
        }

        if (!isPersistent) {
            disposables.add(store.labels.subscribe(labels))
        }
    }

    private fun <Intent : Any> MviStoreBundle<Intent, UiEvent>.handleUiEvent(event: UiEvent) {
        uiEventTransformer?.invoke(event)?.also { store(it) }
    }
}
