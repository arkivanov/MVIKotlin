package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Dependencies
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

class TodoListReaktiveController(dependencies: Dependencies) : TodoListController {

    private val todoListStore =
        TodoListStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database
        ).create()

    private val todoAddStore =
        TodoAddStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database
        ).create()

    init {
        bind(dependencies.lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            eventBus.mapNotNull(BusEvent::toIntent) bindTo todoListStore
            todoAddStore.labels.map(TodoAddStore.Label::toBusEvent) bindTo eventBus
        }

        dependencies.lifecycle.doOnDestroy {
            todoListStore.dispose()
            todoAddStore.dispose()
        }
    }

    override fun onViewCreated(todoListView: TodoListView, todoAddView: TodoAddView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            todoListView.events.map(TodoListView.Event::toIntent) bindTo todoListStore
            todoAddView.events.map(TodoAddView.Event::toIntent) bindTo todoAddStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            todoListStore.states.map(TodoListStore.State::toViewModel) bindTo todoListView
            todoAddStore.states.map(TodoAddStore.State::toViewModel) bindTo todoAddView
        }
    }
}
