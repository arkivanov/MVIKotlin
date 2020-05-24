package com.arkivanov.mvikotlin.sample.todo.common.internal.mapper

import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Model

val addStateToAddModel: State.() -> Model? = { Model(text = text) }

val addEventToAddIntent: Event.() -> Intent? =
    {
        when (this) {
            is Event.TextChanged -> Intent.SetText(text)
            is Event.AddClicked -> Intent.Add
        }
    }
