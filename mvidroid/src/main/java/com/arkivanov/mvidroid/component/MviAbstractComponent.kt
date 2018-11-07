package com.arkivanov.mvidroid.component

import android.support.annotation.MainThread
import com.arkivanov.mvidroid.utils.assertOnMainThread
import com.arkivanov.mvidroid.utils.mapNotNull
import io.reactivex.ObservableSource
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * Abstract implementation of [MviComponent].
 *
 * Responsibilities:
 * * Everything from [MviComponent]
 * * If Labels relay is provided:
 *     * converts Labels to Stores' Intents and redirects them to appropriate Stores
 *     * redirects Labels from non-persistent Stores to Labels relay
 */
abstract class MviAbstractComponent<in Event : Any, out States : Any, Labels> @MainThread constructor(
    private val stores: List<MviStoreBundle<*, Event>>,
    labels: Labels? = null,
    private val onDisposeAction: (() -> Unit)? = null
) : MviComponent<Event, States> where Labels : ObservableSource<out Any>, Labels : Consumer<in Any> {

    private val disposables = CompositeDisposable()

    init {
        assertOnMainThread()
        labels?.also { l ->
            stores.forEach { it.connectLabels(l) }
        }
    }

    override fun accept(event: Event) {
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
        onDisposeAction?.invoke()
    }

    override fun isDisposed(): Boolean {
        assertOnMainThread()

        return disposables.isDisposed
    }

    private fun <Intent : Any> MviStoreBundle<Intent, *>.connectLabels(labels: Labels) {
        labelTransformer?.also { transformer ->
            disposables.add(labels.mapNotNull(transformer).subscribe { store.accept(it) })
        }

        if (!isPersistent) {
            disposables.add(store.labels.subscribe(labels))
        }
    }

    private fun <Intent : Any> MviStoreBundle<Intent, Event>.handleUiEvent(event: Event) {
        eventTransformer?.invoke(event)?.also { store.accept(it) }
    }
}
