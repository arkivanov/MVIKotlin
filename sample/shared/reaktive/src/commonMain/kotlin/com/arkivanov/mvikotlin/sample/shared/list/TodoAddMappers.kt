package com.arkivanov.mvikotlin.sample.shared.list

import com.arkivanov.mvikotlin.sample.shared.BusEvent
import com.arkivanov.mvikotlin.sample.shared.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.shared.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.shared.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView.Model

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
