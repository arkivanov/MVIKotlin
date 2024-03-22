package com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.Intent
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.State
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

internal fun StoreFactory.addStore(
    database: TodoDatabase,
): AddStore =
    object : AddStore, Store<Intent, State, Label> by create(
        name = "TodoAddStore",
        initialState = State(),
        executorFactory = { ExecutorImpl(database) },
        reducer = { reduce(it) },
    ) {}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed class Msg : JvmSerializable {
    data class TextChanged(val text: String) : Msg()
}

private class ExecutorImpl(
    private val database: TodoDatabase,
) : ReaktiveExecutor<Intent, Nothing, State, Msg, Label>() {
    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.SetText -> dispatch(Msg.TextChanged(intent.text))
            is Intent.Add -> addItem()
        }.let {}
    }

    private fun addItem() {
        val text = state().text.takeUnless(String::isBlank) ?: return

        dispatch(Msg.TextChanged(""))

        singleFromFunction {
            database.create(TodoItem.Data(text = text))
        }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .map(Label::Added)
            .subscribeScoped(onSuccess = ::publish)
    }
}

private fun State.reduce(msg: Msg): State =
    when (msg) {
        is Msg.TextChanged -> copy(text = msg.text)
    }
