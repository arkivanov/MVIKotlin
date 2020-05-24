package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStoreAbstractFactory
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoListStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase
) : TodoListStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, State, Result, Nothing> = ExecutorImpl()

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Unit, State, Result, Nothing>() {
        override fun executeAction(action: Unit, getState: () -> State) {
            singleFromFunction(database::getAll)
                .subscribeOn(ioScheduler)
                .map(Result::Loaded)
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.Delete -> delete(intent.id)
                is Intent.ToggleDone -> toggleDone(intent.id, getState)
                is Intent.AddToState -> dispatch(Result.Added(intent.item))
                is Intent.DeleteFromState -> dispatch(Result.Deleted(intent.id))
                is Intent.UpdateInState -> dispatch(Result.Changed(intent.id, intent.data))
            }.let {}
        }

        private fun delete(id: String) {
            dispatch(Result.Deleted(id))

            singleFromFunction { database.delete(id) }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }

        private fun toggleDone(id: String, state: () -> State) {
            dispatch(Result.DoneToggled(id))

            val item = state().items.find { it.id == id } ?: return

            completableFromFunction {
                database.save(id, item.data)
            }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }
    }
}
