package com.arkivanov.mvikotlin.sample.shared.view

import com.arkivanov.mvikotlin.core.view.View
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView.Model

interface TodoAddView : View<Model, Event> {

    data class Model(
        val text: String
    )

    sealed class Event {
        data class TextChanged(val text: String) : Event()
        object AddClicked : Event()
    }
}
