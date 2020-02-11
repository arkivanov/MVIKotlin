package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.State
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStoreAbstractFactory
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal class TodoDetailsStoreFactory(
    storeFactory: StoreFactory,
    private val database: TodoDatabase,
    private val itemId: String
) : TodoDetailsStoreAbstractFactory(
    storeFactory = storeFactory
) {

    override fun createExecutor(): Executor<Intent, Unit, Result, State, Label> = ExecutorImpl()

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Unit, Result, State, Label>() {
        override fun handleAction(action: Unit) {
            singleFromFunction {
                database.get(itemId)
            }
                .subscribeOn(ioScheduler)
                .map { it?.data?.let(Result::Loaded) ?: Result.Finished }
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true, onSuccess = ::dispatch)
        }

        override fun handleIntent(intent: Intent) {
            when (intent) {
                is Intent.HandleTextChanged -> handleTextChanged(intent.text)
                is Intent.ToggleDone -> toggleDone()
                is Intent.Delete -> delete()
            }.let {}
        }

        private fun handleTextChanged(text: String) {
            dispatch(Result.TextChanged(text))
            save()
        }

        private fun toggleDone() {
            dispatch(Result.DoneToggled)
            save()
        }

        private fun save() {
            val data = state.data ?: return
            publish(Label.Changed(itemId, data))

            completableFromFunction {
                database.save(itemId, data)
            }
                .subscribeOn(ioScheduler)
                .subscribeScoped()
        }

        private fun delete() {
            publish(Label.Deleted(itemId))

            completableFromFunction {
                database.delete(itemId)
            }
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribeScoped(isThreadLocal = true) {
                    dispatch(Result.Finished)
                }
        }
    }
}
