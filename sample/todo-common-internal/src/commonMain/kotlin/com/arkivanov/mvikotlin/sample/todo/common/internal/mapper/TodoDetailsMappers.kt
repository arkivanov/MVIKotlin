package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Model

fun State.toViewModel(): Model =
    Model(
        text = data?.text ?: "",
        isDone = data?.isDone ?: false,
        isFlowFinished = isFinished
    )

fun Event.toIntent(): Intent =
    when (this) {
        is Event.TextChanged -> Intent.HandleTextChanged(text = text)
        is Event.DoneClicked -> Intent.ToggleDone
        is Event.DeleteClicked -> Intent.Delete
    }

fun Label.toBusEvent(): BusEvent =
    when (this) {
        is Label.Changed -> BusEvent.TodoItemChanged(id = id, data = data)
        is Label.Deleted -> BusEvent.TodoItemDeleted(id = id)
    }

