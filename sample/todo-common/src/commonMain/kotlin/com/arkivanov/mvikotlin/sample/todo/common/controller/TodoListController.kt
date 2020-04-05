package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView

interface TodoListController {

    fun onViewCreated(todoListView: TodoListView, todoAddView: TodoAddView, viewLifecycle: Lifecycle)

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val lifecycle: Lifecycle
        val stateKeeperProvider: StateKeeperProvider<Any>?
    }
}
