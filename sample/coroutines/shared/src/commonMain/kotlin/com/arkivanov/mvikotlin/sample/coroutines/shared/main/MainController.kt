package com.arkivanov.mvikotlin.sample.coroutines.shared.main

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.AddStoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.ListStore
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.ListStoreFactory
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull

class MainController(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    lifecycle: Lifecycle,
    instanceKeeper: InstanceKeeper,
    private val dispatchers: TodoDispatchers,
    private val onItemSelected: (id: String) -> Unit,
) {

    private val listStore =
        instanceKeeper.getStore {
            ListStoreFactory(
                storeFactory = storeFactory,
                database = database,
                mainContext = dispatchers.main,
                ioContext = dispatchers.io,
            ).create()
        }

    private val addStore =
        instanceKeeper.getStore {
            AddStoreFactory(
                storeFactory = storeFactory,
                database = database,
                mainContext = dispatchers.main,
                ioContext = dispatchers.io,
            ).create()
        }

    init {
        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY, dispatchers.unconfined) {
            addStore.labels.mapNotNull(addLabelToListIntent) bindTo listStore
        }
    }

    fun onViewCreated(view: MainView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY, dispatchers.unconfined) {
            view.events.mapNotNull(eventToListIntent) bindTo listStore
            view.events.mapNotNull(eventToAddIntent) bindTo addStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP, dispatchers.unconfined) {
            combine(listStore.states, addStore.states, statesToModel) bindTo view
            view.events bindTo ::onEvent
        }
    }

    private fun onEvent(event: Event) {
        when (event) {
            is Event.ItemClicked -> onItemSelected(event.id)
            is Event.AddClicked,
            is Event.ItemDeleteClicked,
            is Event.ItemDoneClicked,
            is Event.TextChanged -> null
        }.let {}
    }

    fun onItemChanged(id: String, data: TodoItem.Data) {
        listStore.accept(ListStore.Intent.UpdateInState(id = id, data = data))
    }

    fun onItemDeleted(id: String) {
        listStore.accept(ListStore.Intent.DeleteFromState(id = id))
    }
}
