package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStoreAbstractFactory
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoAddStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase
) : TodoAddStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Nothing, State, Result, Label> = ExecutorImpl()

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SetText -> dispatch(Result.TextChanged(intent.text))
                is Intent.Add -> addItem(getState())
            }.let {}
        }

        private fun addItem(state: State) {
            val text = state.text.takeUnless(String::isBlank) ?: return

            dispatch(Result.TextChanged(""))

            singleFromFunction {
                database.create(TodoItem.Data(text = text))
            }
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .map(Label::Added)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::publish)
        }
    }
}
