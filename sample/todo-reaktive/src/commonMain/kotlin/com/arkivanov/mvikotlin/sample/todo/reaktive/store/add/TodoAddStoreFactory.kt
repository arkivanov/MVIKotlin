package com.arkivanov.mvikotlin.sample.todo.reaktive.store.add

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.reaktive.store.add.TodoAddStore.State
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.scheduler.singleScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoAddStoreFactory(
    private val storeFactory: StoreFactory,
    private val database: TodoDatabase
) {

    fun create(): TodoAddStore =
        object : TodoAddStore, Store<Intent, State, Label> by storeFactory.create(
            name = "TodoAddStore",
            initialState = State(),
            executorFactory = ::Executor,
            reducer = ReducerImpl
        ) {
        }

    private sealed class Result : JvmSerializable {
        data class Text(val text: String) : Result()
    }

    private inner class Executor : ReaktiveExecutor<Intent, Nothing, Result, State, Label>() {
        override fun handleIntent(intent: Intent) {
            when (intent) {
                is Intent.HandleTextChanged -> dispatch(
                    Result.Text(
                        intent.text
                    )
                )
                is Intent.Add -> addItem()
            }.let {}
        }

        private fun addItem() {
            val text = state.text
            if (!text.isBlank()) {
                dispatch(Result.Text(""))

                singleFromFunction {
                    database.put(TodoItem(text = text))
                }
                    .subscribeOn(singleScheduler)
                    .observeOn(mainScheduler)
                    .map(Label::Added)
                    .subscribeScoped(isThreadLocal = true, onSuccess = ::publish)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.Text -> copy(text = result.text)
            }
    }
}
