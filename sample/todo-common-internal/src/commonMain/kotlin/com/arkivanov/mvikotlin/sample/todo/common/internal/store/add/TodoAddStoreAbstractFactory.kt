package com.arkivanov.mvikotlin.sample.todo.common.internal.store.add

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State
import com.badoo.reaktive.utils.ensureNeverFrozen

abstract class TodoAddStoreAbstractFactory(
    private val storeFactory: StoreFactory
) {

    fun create(): TodoAddStore =
        object : TodoAddStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoAddStore",
            initialState = State(),
            executorFactory = ::createExecutor,
            reducer = ReducerImpl
        ) {
            init {
                ensureNeverFrozen()
            }
        }

    protected abstract fun createExecutor(): Executor<Intent, Nothing, Result, State, Label>

    protected sealed class Result : JvmSerializable {
        data class TextChanged(val text: String) : Result()
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.TextChanged -> copy(text = result.text)
            }
    }
}
