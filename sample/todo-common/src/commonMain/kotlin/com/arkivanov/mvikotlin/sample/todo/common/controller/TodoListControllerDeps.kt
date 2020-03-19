package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class TodoListControllerDeps(
    override val storeFactory: StoreFactory,
    override val database: TodoDatabase,
    override val stateKeeperProvider: StateKeeperProvider<Any>? = null
) : TodoListController.Dependencies
