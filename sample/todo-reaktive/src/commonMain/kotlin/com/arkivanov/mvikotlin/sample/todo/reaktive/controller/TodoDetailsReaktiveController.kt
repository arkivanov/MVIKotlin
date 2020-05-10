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
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsLabelToBusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.mapper.detailsStateToModel
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.reaktive.eventBus
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.TodoDetailsStoreFactory
import com.badoo.reaktive.observable.mapNotNull

class TodoDetailsReaktiveController(dependencies: Dependencies) : TodoDetailsController {

    private val todoEditStore =
        TodoDetailsStoreFactory(
            storeFactory = dependencies.storeFactory,
            database = dependencies.database,
            itemId = dependencies.itemId
        ).create()

    init {
        bind(dependencies.lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            todoEditStore.labels.mapNotNull(detailsLabelToBusEvent) bindTo eventBus
        }

        dependencies.lifecycle.doOnDestroy(todoEditStore::dispose)
    }

    override fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            todoDetailsView.events.mapNotNull(detailsEventToIntent) bindTo todoEditStore
        }

        bind(viewLifecycle, BinderLifecycleMode.START_STOP) {
            todoEditStore.states.mapNotNull(detailsStateToModel) bindTo todoDetailsView
        }
    }
}
