package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.Binder
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toBusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toViewModel
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.reaktive.eventBus
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.TodoDetailsStoreFactory
import com.badoo.reaktive.observable.map

class TodoDetailsReaktiveController(
    storeFactory: StoreFactory,
    database: TodoDatabase,
    itemId: String
) : TodoDetailsController {

    private val todoEditStore =
        TodoDetailsStoreFactory(
            storeFactory = storeFactory,
            database = database,
            itemId = itemId
        ).create()

    private val storeBinder =
        bind {
            todoEditStore.labels.map(TodoDetailsStore.Label::toBusEvent) bindTo eventBus
        }

    private var viewBinder: Binder? = null

    init {
        storeBinder.start()
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView) {
        viewBinder =
            bind {
                todoDetailsView.events.map(TodoDetailsView.Event::toIntent) bindTo todoEditStore
                todoEditStore.states.map(TodoDetailsStore.State::toViewModel) bindTo todoDetailsView
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
        todoEditStore.dispose()
    }
}

