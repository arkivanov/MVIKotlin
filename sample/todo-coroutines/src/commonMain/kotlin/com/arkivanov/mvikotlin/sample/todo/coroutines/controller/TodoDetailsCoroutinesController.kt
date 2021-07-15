package com.arkivanov.mvikotlin.sample.todo.coroutines.controller

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
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
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class TodoDetailsCoroutinesController internal constructor(
    private val dependencies: Dependencies,
    private val mainContext: CoroutineContext,
    ioContext: CoroutineContext
) : TodoDetailsController {

    constructor(dependencies: Dependencies) : this(
        dependencies = dependencies,
        mainContext = mainDispatcher,
        ioContext = ioDispatcher
    )

    private val todoDetailsStore =
        TodoDetailsStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            itemId = dependencies.itemId,
            mainContext = mainContext,
            ioContext = ioContext
        ).create()

    init {
        dependencies.lifecycle.doOnDestroy(todoDetailsStore::dispose)
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY, mainContext) {
            todoDetailsView.events.mapNotNull(detailsEventToIntent) bindTo todoDetailsStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP, mainContext) {
            todoDetailsStore.states.mapNotNull(detailsStateToModel) bindTo todoDetailsView
            todoDetailsStore.labels.mapNotNull(detailsLabelToOutput).flatMapConcat { it.asFlow() } bindTo { dependencies.detailsOutput(it) }
        }
    }
}
