package com.arkivanov.mvikotlin.sample.todo.common.internal.store.add

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State

/**
 * Abstract factories are normally not needed. Just create a normal factory as described [here][Store].
 * In MVIKotlin samples abstract factories are used because each `Store` has two implementations: using Reaktive and coroutines.
 */
abstract class TodoAddStoreAbstractFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): TodoAddStore =
        object : TodoAddStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoAddStore",
            initialState = State(),
            executorFactory = ::createExecutor,
            reducer = ReducerImpl
        ){
        }

    protected abstract fun createExecutor(): Executor<Intent, Nothing, State, Msg, Label>

    protected sealed class Msg : JvmSerializable {
        data class TextChanged(val text: String) : Msg()
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.TextChanged -> copy(text = msg.text)
            }
    }
}
