package com.arkivanov.mvikotlin.sample.todo.coroutines.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Dependencies
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
class TodoDetailsCoroutinesController(dependencies: Dependencies) : TodoDetailsController {

    private val todoEditStore =
        TodoDetailsStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            itemId = dependencies.itemId,
            mainContext = mainDispatcher,
            ioContext = ioDispatcher
        ).create()

    init {
        bind(dependencies.lifecycle, BinderLifecycleMode.CREATE_DESTROY, mainDispatcher) {
            todoEditStore.labels.map { it.toBusEvent() } bindTo { eventBus.send(it) }
        }

        dependencies.lifecycle.doOnDestroy(todoEditStore::dispose)
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY, mainDispatcher) {
            todoDetailsView.events.map { it.toIntent() } bindTo todoEditStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP, mainDispatcher) {
            todoEditStore.states.map { it.toViewModel() } bindTo todoDetailsView
        }
    }
}
