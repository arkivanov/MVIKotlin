package com.arkivanov.mvikotlin.sample.todo.coroutines.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStoreAbstractFactory
import kotlinx.coroutines.launch
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

    override fun createExecutor(): Executor<Intent, Nothing, State, Msg, Label> = ExecutorImpl()

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Nothing, State, Msg, Label>(mainContext = mainContext) {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SetText -> dispatch(Msg.TextChanged(intent.text))
                is Intent.Add -> addItem(getState())
            }.let {}
        }

        private fun addItem(state: State) {
            val text = state.text.takeUnless(String::isBlank) ?: return

            dispatch(Msg.TextChanged(""))

            scope.launch {
                val item = withContext(ioContext) { database.create(TodoItem.Data(text = text)) }
                publish(Label.Added(item))
            }
        }
    }
}
