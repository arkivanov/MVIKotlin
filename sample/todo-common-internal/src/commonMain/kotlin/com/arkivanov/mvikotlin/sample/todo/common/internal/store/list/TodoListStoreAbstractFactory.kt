package com.arkivanov.mvikotlin.sample.todo.common.internal.store.list

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.database.update
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State

/**
 * Abstract factories are normally not needed. Just create a normal factory as described [here][Store].
 * In MVIKotlin samples abstract factories are used because each `Store` has two implementations: using Reaktive and coroutines.
 */
abstract class TodoListStoreAbstractFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): TodoListStore =
        object : TodoListStore, Store<Intent, State, Nothing> by storeFactory.create(
            name = "ListStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::createExecutor,
            reducer = ReducerImpl
        ) {
        }

    protected sealed class Msg : JvmSerializable {
        data class Loaded(val items: List<TodoItem>) : Msg()
        data class Deleted(val id: String) : Msg()
        data class DoneToggled(val id: String) : Msg()
        data class Added(val item: TodoItem) : Msg()
        data class Changed(val id: String, val data: TodoItem.Data) : Msg()
    }

    protected abstract fun createExecutor(): Executor<Intent, Unit, State, Msg, Nothing>

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.Loaded -> copy(items = msg.items)
                is Msg.Deleted -> copy(items = items.filterNot { it.id == msg.id })
                is Msg.DoneToggled -> copy(items = items.update(msg.id) { copy(isDone = !isDone) })
                is Msg.Added -> copy(items = items + msg.item)
                is Msg.Changed -> copy(items = items.update(msg.id) { msg.data })
            }
    }
}
