package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class TodoDetailsControllerDeps(
    override val storeFactory: StoreFactory,
    override val database: TodoDatabase,
    override val lifecycle: Lifecycle,
    override val itemId: String,
    override val detailsOutput: (TodoDetailsController.Output) -> Unit
) : TodoDetailsController.Dependencies
