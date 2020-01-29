package com.arkivanov.mvikotlin.sample.todo.common.view

import com.arkivanov.mvikotlin.core.view.View
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Model

interface TodoDetailsView : View<Model, Event> {

    data class Model(
        val text: String,
        val isDone: Boolean,
        val isFlowFinished: Boolean
    )

    sealed class Event {
        data class TextChanged(val text: String) : Event()
        object DoneClicked : Event()
        object DeleteClicked : Event()
    }
}
