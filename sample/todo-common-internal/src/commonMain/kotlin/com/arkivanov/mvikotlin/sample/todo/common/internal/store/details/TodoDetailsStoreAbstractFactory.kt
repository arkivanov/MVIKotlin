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
import com.badoo.reaktive.utils.ensureNeverFrozen

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
            init {
                ensureNeverFrozen() // Optional, just a matter of preference, Stores are freezable
            }
        }

    protected abstract fun createExecutor(): Executor<Intent, Unit, State, Result, Label>

    protected sealed class Result : JvmSerializable {
        data class Loaded(val data: TodoItem.Data) : Result()
        object Finished : Result()
        data class TextChanged(val text: String) : Result()
        object DoneToggled : Result()
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Loaded -> copy(data = result.data)
                is Result.Finished -> copy(isFinished = true)
                is Result.TextChanged -> copy(data = data?.copy(text = result.text))
                is Result.DoneToggled -> copy(data = data?.copy(isDone = !data.isDone))
            }
    }
}
