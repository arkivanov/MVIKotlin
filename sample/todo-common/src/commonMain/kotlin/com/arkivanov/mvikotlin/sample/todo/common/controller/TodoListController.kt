package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView

interface TodoListController {

    fun onViewCreated(
        todoListView: TodoListView,
        todoAddView: TodoAddView,
        viewLifecycle: Lifecycle,
        output: (Output) -> Unit
    )

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val lifecycle: Lifecycle
    }

    sealed class Output {
        data class ItemSelected(val id: String) : Output()
    }
}
