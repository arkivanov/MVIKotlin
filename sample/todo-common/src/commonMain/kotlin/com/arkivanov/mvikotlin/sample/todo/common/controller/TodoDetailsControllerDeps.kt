package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class TodoDetailsControllerDeps(
    override val storeFactory: StoreFactory,
    override val database: TodoDatabase,
    override val itemId: String
) : TodoDetailsController.Dependencies
