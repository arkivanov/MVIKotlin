package com.arkivanov.mvikotlin.sample.todo.common.internal.store.details

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.State

/**
 * Abstract factories are normally not needed. Just create a normal factory as described [here][Store].
 * In MVIKotlin samples abstract factories are used because each `Store` has two implementations: using Reaktive and coroutines.
 */
abstract class TodoDetailsStoreAbstractFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): TodoDetailsStore =
        object : TodoDetailsStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoDetailsStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::createExecutor,
            reducer = ReducerImpl
        ) {
        }

    protected abstract fun createExecutor(): Executor<Intent, Unit, State, Msg, Label>

    protected sealed class Msg : JvmSerializable {
        data class Loaded(val data: TodoItem.Data) : Msg()
        object Finished : Msg()
        data class TextChanged(val text: String) : Msg()
        object DoneToggled : Msg()
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.Loaded -> copy(data = msg.data)
                is Msg.Finished -> copy(isFinished = true)
                is Msg.TextChanged -> copy(data = data?.copy(text = msg.text))
                is Msg.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
            }
    }
}
