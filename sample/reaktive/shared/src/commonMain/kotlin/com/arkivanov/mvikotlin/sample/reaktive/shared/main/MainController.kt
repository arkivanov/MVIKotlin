package com.arkivanov.mvikotlin.sample.reaktive.shared.main

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.addStore
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.list.ListStore
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.list.listStore
import com.badoo.reaktive.observable.combineLatest
import com.badoo.reaktive.observable.mapNotNull

class MainController(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase,
    lifecycle: Lifecycle,
    instanceKeeper: InstanceKeeper,
    private val onItemSelected: (id: String) -> Unit,
) {

    private val listStore = instanceKeeper.getStore { storeFactory.listStore(database = database) }
    private val addStore = instanceKeeper.getStore { storeFactory.addStore(database = database) }

    init {
        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            addStore.labels.mapNotNull(addLabelToListIntent) bindTo listStore
        }
    }

    fun onViewCreated(view: MainView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            view.events.mapNotNull(eventToListIntent) bindTo listStore
            view.events.mapNotNull(eventToAddIntent) bindTo addStore
            view.events bindTo ::onEvent
            combineLatest(listStore.states, addStore.states, statesToModel) bindTo view
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
