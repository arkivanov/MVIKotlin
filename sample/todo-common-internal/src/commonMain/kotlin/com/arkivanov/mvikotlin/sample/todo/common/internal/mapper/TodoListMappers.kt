package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Output
import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Model

fun State.toViewModel(): Model =
    Model(
        items = items
    )

fun Event.toIntent(): Intent? =
    when (this) {
        is Event.ItemDoneClicked -> Intent.ToggleDone(id = id)
        is Event.ItemDeleteClicked -> Intent.Delete(id = id)
        is Event.ItemClicked -> null
    }

fun Event.toOutput(): Output? =
    when (this) {
        is Event.ItemClicked -> Output.ItemSelected(id)
        is Event.ItemDoneClicked,
        is Event.ItemDeleteClicked -> null
    }

fun BusEvent.toIntent(): Intent? =
    when (this) {
        is BusEvent.TodoItemAdded -> Intent.HandleAdded(item = item)
        is BusEvent.TodoItemChanged -> Intent.HandleItemChanged(id = id, data = data)
        is BusEvent.TodoItemDeleted -> Intent.HandleDeleted(id = id)
    }
