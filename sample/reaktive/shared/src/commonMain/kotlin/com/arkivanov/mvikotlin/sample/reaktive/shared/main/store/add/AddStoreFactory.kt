package com.arkivanov.mvikotlin.sample.reaktive.shared.main.store.add

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.reaktive.reaktiveExecutorFactory
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

/**
 * This builder function showcases the way of creating a [Store] *without*
 * using the DSL API and *with* the dedicated interface [AddStore].
 * This option may work better for smaller and simpler stores.
 * The [Intent], [State] and [Label] classes are defined inside the store interface.
 */
internal fun StoreFactory.addStore(
    database: TodoDatabase,
): AddStore =
    object : AddStore, Store<Intent, State, Label> by create(
        name = "TodoAddStore",
        initialState = State(),
        executorFactory = reaktiveExecutorFactory {
            onIntent<Intent.SetText> { dispatch(Msg.TextChanged(it.text)) }

            onIntent<Intent.Add> {
                val text = state().text.takeUnless(String::isBlank) ?: return@onIntent

                dispatch(Msg.TextChanged(""))

                singleFromFunction { database.create(TodoItem.Data(text = text)) }
                    .subscribeOn(ioScheduler)
                    .observeOn(mainScheduler)
                    .map(Label::Added)
                    .subscribeScoped(onSuccess = ::publish)
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
