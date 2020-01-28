package com.arkivanov.mvikotlin.sample.todo.reaktive.list

import com.arkivanov.mvikotlin.sample.todo.reaktive.BusEvent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Model

internal fun State.toViewModel(): Model =
    Model(
        text = text
    )

internal fun Event.toIntent(): Intent =
    when (this) {
        is Event.TextChanged -> Intent.HandleTextChanged(text)
        is Event.AddClicked -> Intent.Add
    }

internal fun TodoAddStore.Label.toBusEvent(): BusEvent =
    when (this) {
        is TodoAddStore.Label.Added -> BusEvent.TodoItemAdded(item)
    }
