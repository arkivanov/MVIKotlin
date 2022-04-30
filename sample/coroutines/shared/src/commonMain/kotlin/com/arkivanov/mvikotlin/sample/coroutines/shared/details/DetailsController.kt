package com.arkivanov.mvikotlin.sample.coroutines.shared.details

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStoreFactory
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

class DetailsController(
    storeFactory: StoreFactory,
    database: TodoDatabase,
    lifecycle: Lifecycle,
    private val dispatchers: TodoDispatchers,
    itemId: String,
    private val onItemChanged: (id: String, data: TodoItem.Data) -> Unit,
    private val onItemDeleted: (id: String) -> Unit,
) {

    private val detailsStore =
        DetailsStoreFactory(
            storeFactory = storeFactory,
            database = database,
            mainContext = dispatchers.main,
            ioContext = dispatchers.io,
            itemId = itemId,
        ).create()

    init {
        lifecycle.doOnDestroy(detailsStore::dispose)

        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY, dispatchers.unconfined) {
            detailsStore.labels bindTo { label ->
                when (label) {
                    is Label.Changed -> onItemChanged(label.id, label.data)
                    is Label.Deleted -> onItemDeleted(label.id)
                }.let {}
            }
        }
    }

    fun onViewCreated(view: DetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP, dispatchers.unconfined) {
            view.events.mapNotNull(eventToIntent) bindTo detailsStore
            detailsStore.states.map(stateToModel) bindTo view
        }
    }
}
