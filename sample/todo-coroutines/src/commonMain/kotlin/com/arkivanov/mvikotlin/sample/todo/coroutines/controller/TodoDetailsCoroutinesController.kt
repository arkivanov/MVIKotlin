package com.arkivanov.mvikotlin.sample.todo.coroutines.controller

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.Binder
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toBusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toViewModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.coroutines.eventBus
import com.arkivanov.mvikotlin.sample.todo.coroutines.ioDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.mainDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoDetailsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
class TodoDetailsCoroutinesController(
    storeFactory: StoreFactory,
    database: TodoDatabase,
    itemId: String
) : TodoDetailsController {

    private val todoEditStore =
        TodoDetailsStoreFactory(
            storeFactory = storeFactory,
            database = database,
            itemId = itemId,
            mainContext = mainDispatcher,
            ioContext = ioDispatcher
        ).create()

    private val storeBinder =
        bind(mainDispatcher) {
            todoEditStore.labels.map { it.toBusEvent() } bindTo { eventBus.send(it) }
        }

    private var viewBinder: Binder? = null

    init {
        storeBinder.start()
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView) {
        viewBinder =
            bind(mainDispatcher) {
                todoDetailsView.events.map { it.toIntent() } bindTo todoEditStore
                todoEditStore.states.map { it.toViewModel() } bindTo todoDetailsView
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
