package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Model

fun State.toViewModel(): Model =
    Model(
        items = items,
        selectedItemId = selectedItemId
    )

fun Event.toIntent(): Intent =
    when (this) {
        is Event.ItemClicked -> Intent.SelectItem(id = id)
        is Event.ItemDoneClicked -> Intent.ToggleDone(id = id)
        is Event.ItemDeleteClicked -> Intent.Delete(id = id)
        is Event.ItemSelectionHandled -> Intent.UnselectItem
    }

fun BusEvent.toIntent(): Intent? =
    when (this) {
        is BusEvent.TodoItemAdded -> Intent.HandleAdded(item = item)
        is BusEvent.TodoItemChanged -> Intent.HandleItemChanged(id = id, data = data)
        is BusEvent.TodoItemDeleted -> Intent.HandleDeleted(id = id)
    }
