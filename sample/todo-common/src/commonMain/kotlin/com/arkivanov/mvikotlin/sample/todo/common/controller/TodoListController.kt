package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView

interface TodoListController {

    val input: (Input) -> Unit

    fun onViewCreated(
        todoListView: TodoListView,
        todoAddView: TodoAddView,
        viewLifecycle: Lifecycle
    )

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val lifecycle: Lifecycle
        val instanceKeeper: InstanceKeeper
        val listOutput: (Output) -> Unit
    }

    sealed class Input {
        data class ItemChanged(val id: String, val data: TodoItem.Data) : Input()
        data class ItemDeleted(val id: String) : Input()
    }

    sealed class Output {
        data class ItemSelected(val id: String) : Output()
    }
}
