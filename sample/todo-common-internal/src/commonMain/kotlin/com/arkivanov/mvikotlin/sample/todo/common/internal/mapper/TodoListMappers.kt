package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Input
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Output
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Model

val listStateToListModel: State.() -> Model? = { Model(items = items) }

val listEventToListIntent: Event.() -> Intent? =
    {
        when (this) {
            is Event.ItemDoneClicked -> Intent.ToggleDone(id = id)
            is Event.ItemDeleteClicked -> Intent.Delete(id = id)
            is Event.ItemClicked -> null
        }
    }

val listEventToOutput: Event.() -> Output? =
    {
        when (this) {
            is Event.ItemClicked -> Output.ItemSelected(id)
            is Event.ItemDoneClicked,
            is Event.ItemDeleteClicked -> null
        }
    }

val inputToListIntent: Input.() -> Intent? =
    {
        when (this) {
            is Input.ItemChanged -> Intent.UpdateInState(id = id, data = data)
            is Input.ItemDeleted -> Intent.DeleteFromState(id = id)
        }
    }

val addLabelToListIntent: TodoAddStore.Label.() -> Intent? =
    {
        when (this) {
            is TodoAddStore.Label.Added -> Intent.AddToState(item)
        }
    }
