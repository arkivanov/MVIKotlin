package com.arkivanov.mvikotlin.sample.todo.coroutines.controller

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.utils.statekeeper.get
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toBusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toViewModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.coroutines.eventBus
import com.arkivanov.mvikotlin.sample.todo.coroutines.ioDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.mainDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoAddStoreFactory
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoListStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

@ExperimentalCoroutinesApi
@FlowPreview
class TodoListCoroutinesController(dependencies: Dependencies) : TodoListController {

    private val todoListStore =
        TodoListStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            mainContext = mainDispatcher,
            ioContext = ioDispatcher
        ).create(stateKeeper = dependencies.stateKeeperProvider?.get())

    private val todoAddStore =
        TodoAddStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            mainContext = mainDispatcher,
            ioContext = ioDispatcher
        ).create()

    private val storeBinder =
        bind(mainDispatcher) {
            eventBus.asFlow().mapNotNull { it.toIntent() } bindTo todoListStore
            todoAddStore.labels.map { it.toBusEvent() } bindTo { eventBus.send(it) }
        }

    private var viewBinder: Binder? = null

    init {
        storeBinder.start()
    }

    override fun onViewCreated(todoListView: TodoListView, todoAddView: TodoAddView) {
        viewBinder =
            bind(mainDispatcher) {
                todoListView.events.map { it.toIntent() } bindTo todoListStore
                todoListStore.states.map { it.toViewModel() } bindTo todoListView

                todoAddView.events.map { it.toIntent() } bindTo todoAddStore
                todoAddStore.states.map { it.toViewModel() } bindTo todoAddView
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
