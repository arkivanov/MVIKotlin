package com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.list

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.database.update
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add.AddStore.Label
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.list.ListStore.Intent
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.list.ListStore.State
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.single.subscribeOn

/**
 * This builder function showcases the way of creating a [Store] *without*
 * using the DSL API and *with* the dedicated interface [AddStore].
 * This option may work better for bigger and more complex stores.
 * The [Intent], [State] and [Label] classes are defined inside the store interface.
 */
internal fun StoreFactory.listStore(
    database: TodoDatabase,
): ListStore =
    object : ListStore, Store<Intent, State, Nothing> by create(
        name = "ListStore",
        initialState = State(),
        bootstrapper = SimpleBootstrapper(Action.Init),
        executorFactory = { ExecutorImpl(database) },
        reducer = { reduce(it) },
    ) {}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed interface Action : JvmSerializable {
    data object Init : Action
    data class SaveItem(val id: String) : Action
}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed interface Msg : JvmSerializable {
    data class Loaded(val items: List<TodoItem>) : Msg
    data class Deleted(val id: String) : Msg
    data class DoneToggled(val id: String) : Msg
    data class Added(val item: TodoItem) : Msg
    data class Changed(val id: String, val data: TodoItem.Data) : Msg
}

private class ExecutorImpl(
    private val database: TodoDatabase
) : ReaktiveExecutor<Intent, Action, State, Msg, Nothing>() {
    override fun executeAction(action: Action) {
        when (action) {
            is Action.Init -> init()
            is Action.SaveItem -> saveItem(id = action.id)
        }
    }

    private fun init() {
        singleFromFunction(database::getAll)
            .subscribeOn(ioScheduler)
            .map(Msg::Loaded)
            .observeOn(mainScheduler)
            .subscribeScoped(onSuccess = ::dispatch)
    }

    private fun saveItem(id: String) {
        val item = state().items.find { it.id == id } ?: return

        completableFromFunction {
            database.save(id, item.data)
        }
            .subscribeOn(ioScheduler)
            .subscribeScoped()
    }

    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.Delete -> delete(intent.id)
            is Intent.ToggleDone -> toggleDone(intent.id)
            is Intent.AddToState -> dispatch(Msg.Added(intent.item))
            is Intent.DeleteFromState -> dispatch(Msg.Deleted(intent.id))
            is Intent.UpdateInState -> dispatch(Msg.Changed(intent.id, intent.data))
        }
    }

    private fun delete(id: String) {
        dispatch(Msg.Deleted(id))

        singleFromFunction { database.delete(id) }
            .subscribeOn(ioScheduler)
            .subscribeScoped()
    }

    private fun toggleDone(id: String) {
        dispatch(Msg.DoneToggled(id))
        forward(Action.SaveItem(id = id))
    }
}

private fun State.reduce(msg: Msg): State =
    when (msg) {
        is Msg.Loaded -> copy(items = msg.items)
        is Msg.Deleted -> copy(items = items.filterNot { it.id == msg.id })
        is Msg.DoneToggled -> copy(items = items.update(msg.id) { copy(isDone = !isDone) })
        is Msg.Added -> copy(items = items + msg.item)
        is Msg.Changed -> copy(items = items.update(msg.id) { msg.data })
    }
