package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
import com.arkivanov.mvikotlin.sample.todo.coroutines.store.TodoAddStoreFactory
import com.arkivanov.mvikotlin.sample.todo.reaktive.TestDatabase
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TodoAddStoreTest {

    private val database = TestDatabase()

    private lateinit var store: TodoAddStore

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun sets_text_in_state_WHEN_Intent_SetText() {
        createStore()

        store.accept(Intent.SetText(text = "text"))

        assertEquals("text", store.state.text)
    }

    @Test
    fun creates_item_in_database_WHEN_Intent_Add() {
        createStore()
        store.accept(Intent.SetText(text = "text"))

        store.accept(Intent.Add)

        val createdItem = database.getAll().first()
        assertEquals(createdItem.data, TodoItem.Data(text = "text", isDone = false))
    }

    @Test
    fun publishes_Label_Added_after_creating_in_database_WHEN_Intent_Add() {
        createStore()
        store.accept(Intent.SetText(text = "text"))
        val labels = ArrayList<Label>()
        store.labels(observer { labels += it })

        store.accept(Intent.Add)

        val createdItem = database.getAll().first()
        assertEquals(listOf<Label>(Label.Added(createdItem)), labels)
    }

    private fun createStore() {
        store =
            TodoAddStoreFactory(
                storeFactory = DefaultStoreFactory(),
                database = database,
                mainContext = Dispatchers.Unconfined,
                ioContext = Dispatchers.Unconfined
            ).create()
    }
}
