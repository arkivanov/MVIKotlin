package com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.State
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * This builder function showcases the way of creating a [Store] *without*
 * using the DSL API and *with* the dedicated interface [AddStore].
 * This option may work better for smaller and simpler stores.
 * The [Intent], [State] and [Label] classes are defined inside the store interface.
 */
internal fun StoreFactory.addStore(
    database: TodoDatabase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext,
): AddStore =
    object : AddStore, Store<Intent, State, Label> by create(
        name = "TodoAddStore",
        initialState = State(),
        executorFactory = coroutineExecutorFactory(mainContext) {
            onIntent<Intent.SetText> { dispatch(Msg.TextChanged(it.text)) }

            onIntent<Intent.Add> {
                val text = state().text.takeUnless(String::isBlank) ?: return@onIntent

                dispatch(Msg.TextChanged(""))

                launch {
                    val item = withContext(ioContext) { database.create(TodoItem.Data(text = text)) }
                    publish(Label.Added(item))
                }
            }
        },
        reducer = { msg: Msg ->
            when (msg) {
                is Msg.TextChanged -> copy(text = msg.text)
            }
        },
    ) {}

// Serializable only for exporting events in Time Travel, no need otherwise.
private sealed class Msg : JvmSerializable {
    data class TextChanged(val text: String) : Msg()
}
