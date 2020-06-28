package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.list.TodoListStore.Intent
import com.arkivanov.mvikotlin.sample.todo.reaktive.TestDatabase
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TodoListStoreTest {

    private val database = TestDatabase()

    private lateinit var store: TodoListStore

    @BeforeTest
    fun before() {
        overrideSchedulers(main = { TestScheduler() }, io = { TestScheduler() })
        reaktiveUncaughtErrorHandler = { throw it }
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        overrideSchedulers()
        resetReaktiveUncaughtErrorHandler()
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun loads_items_from_database_WHEN_created() {
        val items = List(100) { TodoItem.Data(text = "text$it", isDone = it % 2 == 0) }
        items.forEach { database.create(it) }

        createStore()

        assertEquals(items, store.state.items.map(TodoItem::data))
    }

    @Test
    fun deletes_item_from_database_WHEN_Intent_Delete() {
        val id = database.create(TodoItem.Data(text = "text")).id
        createStore()

        store.accept(Intent.Delete(id = id))

        assertNull(database.get(id = id))
    }

    @Test
    fun deletes_item_from_state_WHEN_Intent_Delete() {
        val id = database.create(TodoItem.Data(text = "text")).id
        createStore()

        store.accept(Intent.Delete(id = id))

        assertFalse(store.state.items.any { it.id == id })
    }

    @Test
    fun toggles_item_done_in_database_WHEN_Intent_ToggleDone() {
        val id1 = database.create(TodoItem.Data(text = "text1", isDone = false)).id
        val id2 = database.create(TodoItem.Data(text = "text2", isDone = true)).id
        createStore()

        store.accept(Intent.ToggleDone(id = id1))
        store.accept(Intent.ToggleDone(id = id2))

        assertTrue(database.get(id = id1)!!.data.isDone)
        assertFalse(database.get(id = id2)!!.data.isDone)
    }

    @Test
    fun toggles_item_done_in_state_WHEN_Intent_ToggleDone() {
        val id1 = database.create(TodoItem.Data(text = "text1", isDone = false)).id
        val id2 = database.create(TodoItem.Data(text = "text2", isDone = true)).id
        createStore()

        store.accept(Intent.ToggleDone(id = id1))
        store.accept(Intent.ToggleDone(id = id2))

        assertTrue(store.state.items.first { it.id == id1 }.data.isDone)
        assertFalse(store.state.items.first { it.id == id2 }.data.isDone)
    }

    @Test
    fun adds_item_to_state_WHEN_Intent_AddToState() {
        val oldItem = database.create(TodoItem.Data(text = "text1"))
        val newItem = TodoItem(id = "id2", data = TodoItem.Data(text = "text2"))
        createStore()

        store.accept(Intent.AddToState(item = newItem))

        assertEquals(listOf(oldItem, newItem), store.state.items)
    }

    @Test
    fun deletes_item_from_state_WHEN_Intent_DeleteFromState() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        val item3 = database.create(TodoItem.Data(text = "text3"))
        createStore()

        store.accept(Intent.DeleteFromState(id = item2.id))

        assertEquals(listOf(item1, item3), store.state.items)
    }

    @Test
    fun updates_item_in_state_WHEN_Intent_UpdateInState() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2", isDone = false))
        val newData2 = TodoItem.Data(text = "text22", isDone = true)
        val item3 = database.create(TodoItem.Data(text = "text3"))
        createStore()

        store.accept(Intent.UpdateInState(id = item2.id, data = newData2))

        assertEquals(listOf(item1, item2.copy(data = newData2), item3), store.state.items)
    }

    private fun createStore() {
        store = TodoListStoreFactory(DefaultStoreFactory, database).create()
    }
}
