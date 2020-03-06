package com.arkivanov.mvikotlin.sample.todo.common.view

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Model

interface TodoAddView : MviView<Model, Event> {

    data class Model(
        val text: String
    )

    sealed class Event {
        data class TextChanged(val text: String) : Event()
        object AddClicked : Event()
    }
}
