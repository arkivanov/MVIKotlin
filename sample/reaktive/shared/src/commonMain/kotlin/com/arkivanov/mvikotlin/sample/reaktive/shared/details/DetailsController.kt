package com.arkivanov.mvikotlin.sample.reaktive.shared.details

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.store.DetailsStoreFactory
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull

class DetailsController(
    storeFactory: StoreFactory,
    database: TodoDatabase,
    lifecycle: Lifecycle,
    itemId: String,
    private val onItemChanged: (id: String, data: TodoItem.Data) -> Unit,
    private val onItemDeleted: (id: String) -> Unit,
) {

    private val detailsStore =
        DetailsStoreFactory(
            storeFactory = storeFactory,
            database = database,
            itemId = itemId,
        ).create()

    init {
        lifecycle.doOnDestroy(detailsStore::dispose)

        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            detailsStore.labels bindTo { label ->
                when (label) {
                    is Label.Changed -> onItemChanged(label.id, label.data)
                    is Label.Deleted -> onItemDeleted(label.id)
                }.let {}
            }
        }
    }

    fun onViewCreated(view: DetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            view.events.mapNotNull(eventToIntent) bindTo detailsStore
            detailsStore.states.map(stateToModel) bindTo view
        }
    }
}
