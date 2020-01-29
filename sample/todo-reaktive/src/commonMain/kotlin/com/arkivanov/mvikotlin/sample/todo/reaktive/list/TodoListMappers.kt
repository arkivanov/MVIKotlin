package com.arkivanov.mvikotlin.sample.todo.reaktive.list

import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Model
import com.arkivanov.mvikotlin.sample.todo.reaktive.BusEvent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.list.TodoListStore.State

internal fun State.toViewModel(): Model =
    Model(
        items = items,
        selectedItemId = selectedItemId
    )

internal fun Event.toIntent(): Intent =
    when (this) {
        is Event.ItemClicked -> Intent.SelectItem(id = id)
        is Event.ItemDoneClicked -> Intent.ToggleDone(id = id)
        is Event.ItemDeleteClicked -> Intent.Delete(id = id)
        is Event.ItemSelectionHandled -> Intent.UnselectItem
    }

internal fun BusEvent.toIntent(): Intent? =
    when (this) {
        is BusEvent.TodoItemAdded -> Intent.HandleAdded(item = item)
        is BusEvent.TodoItemChanged -> Intent.HandleItemChanged(id = id, data = data)
        is BusEvent.TodoItemDeleted -> Intent.HandleDeleted(id = id)
    }
