package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView

interface TodoDetailsController {

    fun onViewCreated(todoDetailsView: TodoDetailsView, viewLifecycle: Lifecycle)

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val lifecycle: Lifecycle
        val itemId: String
    }
}
