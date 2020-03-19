package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.core.utils.statekeeper.get
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toBusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toViewModel
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.reaktive.eventBus
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.TodoAddStoreFactory
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.TodoListStoreFactory
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull

class TodoListReaktiveController(
    storeFactory: StoreFactory,
    stateKeeperProvider: StateKeeperProvider<Any>?,
    database: TodoDatabase
) : TodoListController {

    private val todoListStore =
        TodoListStoreFactory(
            storeFactory = storeFactory,
            database = database
        ).create(stateKeeper = stateKeeperProvider?.get())

    private val todoAddStore =
        TodoAddStoreFactory(
            storeFactory = storeFactory,
            database = database
        ).create()

    private val storeBinder =
        bind {
            eventBus.mapNotNull(BusEvent::toIntent) bindTo todoListStore
            todoAddStore.labels.map(TodoAddStore.Label::toBusEvent) bindTo eventBus
        }

    private var viewBinder: Binder? = null

    init {
        storeBinder.start()
    }

    override fun onViewCreated(todoListView: TodoListView, todoAddView: TodoAddView) {
        viewBinder =
            bind {
                todoListView.events.map(TodoListView.Event::toIntent) bindTo todoListStore
                todoListStore.states.map(TodoListStore.State::toViewModel) bindTo todoListView

                todoAddView.events.map(TodoAddView.Event::toIntent) bindTo todoAddStore
                todoAddStore.states.map(TodoAddStore.State::toViewModel) bindTo todoAddView
            }
    }

    override fun onStart() {
        viewBinder?.start()
    }

    override fun onStop() {
        viewBinder?.stop()
    }

    override fun onViewDestroyed() {
        viewBinder = null
    }

    override fun onDestroy() {
        storeBinder.stop()
        todoListStore.dispose()
        todoAddStore.dispose()
    }
}
