package com.arkivanov.mvikotlin.sample.todo.reaktive.details

import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Model
import com.arkivanov.mvikotlin.sample.todo.reaktive.BusEvent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.details.TodoDetailsStore.State

internal fun State.toViewModel(): Model =
    Model(
        text = data?.text ?: "",
        isDone = data?.isDone ?: false,
        isFlowFinished = isFinished
    )

internal fun Event.toIntent(): Intent =
    when (this) {
        is Event.TextChanged -> Intent.HandleTextChanged(text = text)
        is Event.DoneClicked -> Intent.ToggleDone
        is Event.DeleteClicked -> Intent.Delete
    }

internal fun Label.toBusEvent(): BusEvent =
    when (this) {
        is Label.Changed -> BusEvent.TodoItemChanged(id = id, data = data)
        is Label.Deleted -> BusEvent.TodoItemDeleted(id = id)
    }

