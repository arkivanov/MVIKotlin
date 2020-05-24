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
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsEventToIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsLabelToOutput
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsStateToModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.coroutines.ioDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.mainDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.mapNotNull
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoDetailsStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat

@ExperimentalCoroutinesApi
class TodoDetailsCoroutinesController(
    private val dependencies: Dependencies
) : TodoDetailsController {

    private val todoDetailsStore =
        TodoDetailsStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            itemId = dependencies.itemId,
            mainContext = mainDispatcher,
            ioContext = ioDispatcher
        ).create()

    init {
        dependencies.lifecycle.doOnDestroy(todoDetailsStore::dispose)
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY, mainDispatcher) {
            todoDetailsView.events.mapNotNull(detailsEventToIntent) bindTo todoDetailsStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP, mainDispatcher) {
            todoDetailsStore.states.mapNotNull(detailsStateToModel) bindTo todoDetailsView
            todoDetailsStore.labels.mapNotNull(detailsLabelToOutput).flatMapConcat { it.asFlow() } bindTo { dependencies.detailsOutput(it) }
        }
    }
}
