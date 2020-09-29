package com.arkivanov.mvikotlin.sample.todo.coroutines.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.getOrCreateStore
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Input
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.addEventToAddIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.addLabelToListIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.addStateToAddModel
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.inputToListIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.listEventToListIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.listEventToOutput
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.listStateToListModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.coroutines.ioDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.mainDispatcher
import com.arkivanov.mvikotlin.sample.todo.coroutines.mapNotNull
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoAddStoreFactory
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoListStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@FlowPreview
class TodoListCoroutinesController internal constructor(
    private val dependencies: Dependencies,
    private val mainContext: CoroutineContext,
    ioContext: CoroutineContext
) : TodoListController {

    constructor(dependencies: Dependencies) : this(
        dependencies = dependencies,
        mainContext = mainDispatcher,
        ioContext = ioDispatcher
    )

    private val todoListStore =
        dependencies.instanceKeeper.getOrCreateStore {
            TodoListStoreFactory(
                storeFactory = dependencies.storeFactory,
                database = dependencies.database,
                mainContext = mainContext,
                ioContext = ioContext
            ).create()
        }

    private val todoAddStore =
        TodoAddStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            mainContext = mainContext,
            ioContext = ioContext
        ).create()

    private val inputChannel = BroadcastChannel<Input>(Channel.BUFFERED)
    override val input: (Input) -> Unit = { inputChannel.offer(it) }

    init {
        bind(dependencies.lifecycle, BinderLifecycleMode.CREATE_DESTROY, mainContext) {
            inputChannel.asFlow().mapNotNull(inputToListIntent) bindTo todoListStore
            todoAddStore.labels.mapNotNull(addLabelToListIntent) bindTo todoListStore
        }

        dependencies.lifecycle.doOnDestroy(todoAddStore::dispose)
    }

    override fun onViewCreated(
        todoListView: TodoListView,
        todoAddView: TodoAddView,
        viewLifecycle: Lifecycle
    ) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY, mainContext) {
            todoListView.events.mapNotNull(listEventToListIntent) bindTo todoListStore
            todoAddView.events.mapNotNull(addEventToAddIntent) bindTo todoAddStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP, mainContext) {
            todoListStore.states.mapNotNull(listStateToListModel) bindTo todoListView
            todoAddStore.states.mapNotNull(addStateToAddModel) bindTo todoAddView
            todoListView.events.mapNotNull(listEventToOutput) bindTo { dependencies.listOutput(it) }
        }
    }
}
