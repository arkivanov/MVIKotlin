package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.internal.BusEvent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Model

fun State.toViewModel(): Model =
    Model(
        text = text
    )

fun Event.toIntent(): Intent? =
    when (this) {
        is Event.TextChanged -> Intent.HandleTextChanged(text)
        is Event.AddClicked -> Intent.Add
    }

fun TodoAddStore.Label.toBusEvent(): BusEvent? =
    when (this) {
        is TodoAddStore.Label.Added -> BusEvent.TodoItemAdded(item)
    }
