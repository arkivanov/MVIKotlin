package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView

interface TodoDetailsController {

    fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle)

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val lifecycle: Lifecycle
        val itemId: String
        val detailsOutput: (Output) -> Unit
    }

    sealed class Output {
        object Finished : Output()
        data class ItemChanged(val id: String, val data: TodoItem.Data) : Output()
        data class ItemDeleted(val id: String) : Output()
    }
}
