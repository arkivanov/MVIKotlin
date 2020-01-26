package com.arkivanov.mvikotlin.sample.shared.list

import com.arkivanov.mvikotlin.sample.shared.BusEvent
import com.arkivanov.mvikotlin.sample.shared.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.shared.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView.Model

internal fun State.toViewModel(): Model =
    Model(
        items = items,
        selectedItemId = selectedItemId
    )

internal fun Event.toIntent(): Intent =
    when (this) {
        is Event.ItemClicked -> Intent.SelectItem(id)
        is Event.ItemDoneClicked -> Intent.ToggleDone(id)
        is Event.ItemDeleteClicked -> Intent.Delete(id)
        is Event.ItemSelectionHandled -> Intent.UnselectItem
    }

internal fun BusEvent.toIntent(): Intent? =
    when (this) {
        is BusEvent.TodoItemAdded -> Intent.HandleAdded(item)
    }
