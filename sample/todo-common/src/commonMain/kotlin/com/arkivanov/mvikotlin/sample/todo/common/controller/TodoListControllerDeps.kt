package com.arkivanov.mvikotlin.sample.todo.common.controller

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase

class TodoListControllerDeps(
    override val storeFactory: StoreFactory,
    override val database: TodoDatabase,
    override val lifecycle: Lifecycle,
    override val instanceKeeper: InstanceKeeper,
    override val listOutput: (TodoListController.Output) -> Unit
) : TodoListController.Dependencies
