package com.arkivanov.mvikotlin.sample.todo.reaktive.list

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.Binder
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.reaktive.BusEvent
import com.arkivanov.mvikotlin.sample.todo.reaktive.eventBus
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStoreFactory
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.list.TodoListStore
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.list.TodoListStoreFactory
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull

class TodoListController(
    storeFactory: StoreFactory,
    database: TodoDatabase
) {

    private val todoListStore =
        TodoListStoreFactory(
            storeFactory = storeFactory,
            database = database
        ).create()

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

    fun onViewCreated(todoListView: TodoListView, todoAddView: TodoAddView) {
        viewBinder =
            bind {
                todoListView.events.map(TodoListView.Event::toIntent) bindTo todoListStore
                todoListStore.states.map(TodoListStore.State::toViewModel) bindTo todoListView

                todoAddView.events.map(TodoAddView.Event::toIntent) bindTo todoAddStore
                todoAddStore.states.map(TodoAddStore.State::toViewModel) bindTo todoAddView
            }
    }

    fun onStart() {
        viewBinder?.start()
    }

    fun onStop() {
        viewBinder?.stop()
    }

    fun onViewDestroyed() {
        viewBinder = null
    }

    fun onDestroy() {
        storeBinder.stop()
        todoListStore.dispose()
        todoAddStore.dispose()
    }
}
