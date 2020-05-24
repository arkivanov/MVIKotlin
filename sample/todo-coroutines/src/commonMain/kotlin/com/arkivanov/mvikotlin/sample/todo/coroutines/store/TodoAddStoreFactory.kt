package com.arkivanov.mvikotlin.sample.todo.coroutines.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.SuspendExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStoreAbstractFactory
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class TodoAddStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext
) : TodoAddStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Nothing, State, Result, Label> = ExecutorImpl()

    private inner class ExecutorImpl : SuspendExecutor<Intent, Nothing, State, Result, Label>(mainContext = mainContext) {
        override suspend fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SetText -> dispatch(Result.TextChanged(intent.text))
                is Intent.Add -> addItem(getState())
            }.let {}
        }

        private suspend fun addItem(state: State) {
            val text = state.text.takeUnless(String::isBlank) ?: return

            dispatch(Result.TextChanged(""))

            val item =
                withContext(ioContext) {
                    database.create(TodoItem.Data(text = text))
                }

            publish(Label.Added(item))
        }
    }
}
