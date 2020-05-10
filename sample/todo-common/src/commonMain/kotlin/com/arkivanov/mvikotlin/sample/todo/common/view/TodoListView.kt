package com.arkivanov.mvikotlin.sample.todo.common.view

import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Model

interface TodoListView : MviView<Model, Event> {

    data class Model(
        val items: List<TodoItem>
    )

    sealed class Event {
        data class ItemClicked(val id: String) : Event()
        data class ItemDoneClicked(val id: String) : Event()
        data class ItemDeleteClicked(val id: String) : Event()
    }
}
