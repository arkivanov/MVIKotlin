package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.details.TodoDetailsStore.Label
import com.arkivanov.mvikotlin.sample.todo.reaktive.TestDatabase
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TodoDetailsStoreTest {

    private lateinit var itemId: String
    private val itemData = TodoItem.Data(text = "text", isDone = false)
    private val database = TestDatabase()

    private lateinit var store: TodoDetailsStore

    @BeforeTest
    fun before() {
        overrideSchedulers(main = { TestScheduler() }, io = { TestScheduler() })
        reaktiveUncaughtErrorHandler = { throw it }
        isAssertOnMainThreadEnabled = false

        itemId = database.create(itemData).id
    }

    @AfterTest
    fun after() {
        overrideSchedulers()
        resetReaktiveUncaughtErrorHandler()
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun loads_item_from_database_WHEN_created() {
        createStore()

        assertEquals(itemData, store.state.data)
    }

    @Test
    fun updates_text_in_database_WHEN_Intent_SetText() {
        createStore()

        store.accept(Intent.SetText(text = "new_text"))

        assertEquals(itemData.copy(text = "new_text"), database.get(id = itemId)!!.data)
    }

    @Test
    fun sets_text_in_state_WHEN_Intent_SetText() {
        createStore()

        store.accept(Intent.SetText(text = "new_text"))

        assertEquals(itemData.copy(text = "new_text"), store.state.data)
    }

    @Test
    fun publishes_Label_Changed_WHEN_Intent_SetText() {
        createStore()
        val labels = store.labels.test(autoFreeze = false)

        store.accept(Intent.SetText(text = "new_text"))

        labels.assertValue(Label.Changed(id = itemId, data = itemData.copy(text = "new_text")))
    }

    @Test
    fun toggles_isDone_from_false_to_true_in_database_WHEN_Intent_ToggleDone() {
        createStore()

        store.accept(Intent.ToggleDone)

        assertEquals(itemData.copy(isDone = true), database.get(id = itemId)!!.data)
    }

    @Test
    fun toggles_isDone_from_true_to_false_in_database_WHEN_Intent_ToggleDone() {
        database.save(id = itemId, data = itemData.copy(isDone = true))
        createStore()

        store.accept(Intent.ToggleDone)

        assertEquals(itemData, database.get(id = itemId)!!.data)
    }

    @Test
    fun toggle_isDone_from_false_to_true_in_state_WHEN_Intent_ToggleDone() {
        createStore()

        store.accept(Intent.ToggleDone)

        assertEquals(itemData.copy(isDone = true), store.state.data)
    }

    @Test
    fun toggle_isDone_from_true_to_false_in_state_WHEN_Intent_ToggleDone() {
        database.save(id = itemId, data = itemData.copy(isDone = true))
        createStore()

        store.accept(Intent.ToggleDone)

        assertEquals(itemData, store.state.data)
    }

    @Test
    fun publishes_Label_Changed_WHEN_Intent_ToggleDone() {
        createStore()
        val labels = store.labels.test(autoFreeze = false)

        store.accept(Intent.ToggleDone)

        labels.assertValue(Label.Changed(id = itemId, data = itemData.copy(isDone = true)))
    }

    @Test
    fun deletes_item_from_database_WHEN_Intent_Delete() {
        createStore()

        store.accept(Intent.Delete)

        assertNull(database.get(id = itemId))
    }

    @Test
    fun publishes_Label_Deleted_WHEN_Intent_Delete() {
        createStore()
        val labels = store.labels.test(autoFreeze = false)

        store.accept(Intent.Delete)

        labels.assertValue(Label.Deleted(id = itemId))
    }

    private fun createStore() {
        store = TodoDetailsStoreFactory(DefaultStoreFactory, database, itemId = itemId).create()
    }
}
