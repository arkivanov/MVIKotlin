package com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.store.add.AddStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.test
import com.arkivanov.mvikotlin.sample.database.MemoryTodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AddStoreTest {

    private val database = MemoryTodoDatabase()

    private lateinit var store: AddStore

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
        val labels = store.labels.test()

        store.accept(Intent.Add)

        val createdItem = database.getAll().first()
        assertContentEquals(listOf(Label.Added(createdItem)), labels)
    }

    private fun createStore() {
        store =
            DefaultStoreFactory().addStore(
                database = database,
                mainContext = Dispatchers.Unconfined,
                ioContext = Dispatchers.Unconfined,
            )
    }
}
