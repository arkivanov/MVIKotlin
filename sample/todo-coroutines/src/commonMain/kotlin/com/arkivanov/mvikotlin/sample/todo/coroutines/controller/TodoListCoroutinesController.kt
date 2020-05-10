package com.arkivanov.mvikotlin.sample.todo.coroutines.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Output
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toBusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.toOutput
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
        ).create()

    private val todoAddStore =
        TodoAddStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            mainContext = mainDispatcher,
            ioContext = ioDispatcher
        ).create()

    init {
        bind(dependencies.lifecycle, BinderLifecycleMode.CREATE_DESTROY, mainDispatcher) {
            eventBus.asFlow().mapNotNull { it.toIntent() } bindTo todoListStore
            todoAddStore.labels.mapNotNull { it.toBusEvent() } bindTo { eventBus.send(it) }
        }

        dependencies.lifecycle.doOnDestroy {
            todoListStore.dispose()
            todoAddStore.dispose()
        }
    }

    override fun onViewCreated(
        todoListView: TodoListView,
        todoAddView: TodoAddView,
        viewLifecycle: Lifecycle,
        output: (Output) -> Unit
    ) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY, mainDispatcher) {
            todoListView.events.mapNotNull { it.toIntent() } bindTo todoListStore
            todoAddView.events.mapNotNull { it.toIntent() } bindTo todoAddStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP, mainDispatcher) {
            todoListStore.states.map { it.toViewModel() } bindTo todoListView
            todoAddStore.states.map { it.toViewModel() } bindTo todoAddView
            todoListView.events.mapNotNull { it.toOutput() } bindTo { output(it) }
        }
    }
}
