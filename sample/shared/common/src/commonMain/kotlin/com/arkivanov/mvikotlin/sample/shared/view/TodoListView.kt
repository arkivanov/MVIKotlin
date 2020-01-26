package com.arkivanov.mvikotlin.sample.shared.view

import com.arkivanov.mvikotlin.core.view.View
import com.arkivanov.mvikotlin.sample.shared.database.TodoItem
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView.Model

interface TodoListView : View<Model, Event> {

    data class Model(
        val items: List<TodoItem>,
        val selectedItemId: String?
    )

    sealed class Event {
        data class ItemClicked(val id: String) : Event()
        data class ItemDoneClicked(val id: String) : Event()
        data class ItemDeleteClicked(val id: String) : Event()
        object ItemSelectionHandled : Event()
    }
}
