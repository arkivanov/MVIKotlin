package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsEventToIntent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsLabelToOutput
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsStateToModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.TodoDetailsStoreFactory
import com.badoo.reaktive.observable.flatten
import com.badoo.reaktive.observable.mapNotNull

class TodoDetailsReaktiveController(
    private val dependencies: Dependencies
) : TodoDetailsController {

    private val todoDetailsStore =
        TodoDetailsStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            itemId = dependencies.itemId
        ).create()

    init {
        dependencies.lifecycle.doOnDestroy(todoDetailsStore::dispose)
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            todoDetailsView.events.mapNotNull(detailsEventToIntent) bindTo todoDetailsStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            todoDetailsStore.states.mapNotNull(detailsStateToModel) bindTo todoDetailsView
            todoDetailsStore.labels.mapNotNull(detailsLabelToOutput).flatten() bindTo dependencies.detailsOutput
        }
    }
}
