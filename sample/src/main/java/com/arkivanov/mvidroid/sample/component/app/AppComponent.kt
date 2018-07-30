package com.arkivanov.mvidroid.sample.component.app

import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.sample.component.createComponent
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStore
import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionStoreProvider
import com.arkivanov.mvidroid.sample.store.todolist.TodoListStore
import com.arkivanov.mvidroid.sample.store.todolist.TodoListStoreProvider
import toothpick.Scope

interface AppComponent : MviComponent<Nothing, Nothing> {

    companion object {
        fun create(appScope: Scope): AppComponent =
            createComponent<AppComponentImpl>(appScope) {
                bind(TodoListStore::class.java)
                    .toProvider(TodoListStoreProvider::class.java)
                    .providesSingletonInScope()

                bind(TodoActionStore::class.java)
                    .toProvider(TodoActionStoreProvider::class.java)
                    .providesSingletonInScope()
            }
    }
}
