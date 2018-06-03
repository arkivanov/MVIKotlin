package com.arkivanov.mvidroid.boundary

import android.support.annotation.MainThread
import com.arkivanov.kfunction.KFunction
import com.arkivanov.mvidroid.boundary.MviBoundary.StoreBundle
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.utils.Disposables
import com.arkivanov.mvidroid.utils.assertOnMainThread
import com.arkivanov.mvidroid.utils.mapNullable
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Boundary is a gluing layer between Stores and UI.
 *
 * Responsibilities:
 * * transforms UI Events and Labels to Intents
 * using provided transformers and redirects Intents to appropriate Stores
 * * forwards Labels from Stores to Labels relay
 * * takes care of disposing Stores
 *
 * It is also the best place to map Stores' states to View Models and provide them to UI.
 * Must be created on Main thread.
 *
 * Implementation example:
 * ```
 * class ExampleBoundary(
 *     store: ExampleStore,
 *     events: Observable<UiEvent>,
 *     labels: Relay<Any>
 * ) : MviBoundary<ExampleBoundary.UiEvent>(
 *     stores = listOf(StoreBundle(store, EventTransformer, LabelTransformer)),
 *     events = events,
 *     labels = labels
 * ) {
 *
 *     val viewModels: Observable<ViewModel> = store.states.map(ViewModelMapper::apply)
 *
 *     sealed class UiEvent {
 *         // Your UI Events here
 *     }
 *
 *     data class ViewModel()
 *
 *     private object ViewModelMapper : Function<ExampleStore.State, ViewModel> {
 *         override fun apply(state: ExampleStore.State): ViewModel {
 *             // Map State to View Model
 *         }
 *     }
 *
 *     private object EventTransformer : KFunction<UiEvent, ExampleStore.Intent?> {
 *         override fun invoke(event: UiEvent): ExampleStore.Intent? {
 *             // Map UI Event to Intent or return null if there is no corresponding Intent
 *         }
 *     }
 *
 *     private object LabelTransformer : KFunction<Any, ExampleStore.Intent?> {
 *         override fun invoke(label: Any): ExampleStore.Intent? {
 *             // Map Label to Intent or return null if there is no corresponding Intent
 *         }
 *     }
 * }
 * ```
 *
 * @param stores a list of [StoreBundle], which is Store itself plus associated UI Event and Label transformers
 * @param events Observable of UI Events, if null then no UI Events will be transformed to Intents
 * and any provided UI Event transformers will not be used. Emissions must be performed only on Main thread.
 * @param labels Relay of Labels, if null then no Labels will be transformed to Intents
 * and any provided Label transformers will not be used. Emissions must be performed only on Main thread.
 * @param E type of UI Events
 */
abstract class MviBoundary<E : Any>(
    private val stores: List<StoreBundle<*, E>>,
    events: Observable<out E>? = null,
    labels: Relay<Any>? = null
) : Disposable {

    private val disposables = Disposables()

    init {
        assertOnMainThread()
        stores.forEach { connectModel(it, events, labels) }
    }

    private fun <I : Any> connectModel(storeBundle: StoreBundle<I, E>, events: Observable<out E>?, labels: Relay<Any>?) {
        events?.also { source ->
            storeBundle.eventTransformer?.also { transformer ->
                disposables.add(source.mapNullable(transformer).subscribe { storeBundle.store(it) })
            }
        }

        labels?.also { relay ->
            storeBundle.labelTransformer?.also { transformer ->
                disposables.add(relay.mapNullable(transformer).subscribe { storeBundle.store(it) })
            }
            disposables.add(storeBundle.store.labels.subscribe(relay))
        }
    }

    /**
     * Disposes this Boundary and all its non-persistent Stores, must be called only on Main thread
     */
    @MainThread
    override fun dispose() {
        disposables.dispose()
        stores.forEach {
            if (!it.isPersistent) {
                it.store.dispose()
            }
        }
    }

    /**
     * Checks whether Boundary is disposed or not, must be called only on Main thread
     *
     * @return true if Boundary is disposed, false otherwise
     */
    @MainThread
    override fun isDisposed(): Boolean = disposables.isDisposed

    /**
     * A holder for Store and its UI Event And Label transformers
     *
     * @param store a Store
     * @param eventTransformer a function that transforms UI Events to Store's Intents, returns null if there is no corresponding mapping
     * @param labelTransformer a function that transforms Labels to Store's Intents, returns null if there is no corresponding mapping
     * @param isPersistent if true then this Store will not be disposed by this Boundary
     * @param I type of Store's Intents
     * @param E type of Boundary's UI Events
     */
    class StoreBundle<I : Any, in E : Any>(
        val store: MviStore<*, I, *>,
        val eventTransformer: KFunction<E, I?>? = null,
        val labelTransformer: KFunction<Any, I?>? = null,
        val isPersistent: Boolean = false
    )
}
