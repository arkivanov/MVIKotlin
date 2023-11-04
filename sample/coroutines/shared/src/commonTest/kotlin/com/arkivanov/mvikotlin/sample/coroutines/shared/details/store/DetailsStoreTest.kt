package com.arkivanov.mvikotlin.sample.coroutines.shared.details.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Intent
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.store.DetailsStore.Label
import com.arkivanov.mvikotlin.sample.coroutines.shared.test
import com.arkivanov.mvikotlin.sample.database.MemoryTodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DetailsStoreTest {

    private lateinit var itemId: String
    private val itemData = TodoItem.Data(text = "text", isDone = false)
    private val database = MemoryTodoDatabase()

    private lateinit var store: DetailsStore

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false

        itemId = database.create(itemData).id
    }

    @AfterTest
    fun after() {
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
        val labels = store.labels.test()

        store.accept(Intent.SetText(text = "new_text"))

        assertContentEquals(listOf(Label.Changed(id = itemId, data = itemData.copy(text = "new_text"))), labels)
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
        val labels = store.labels.test()

        store.accept(Intent.ToggleDone)

        assertContentEquals(listOf(Label.Changed(id = itemId, data = itemData.copy(isDone = true))), labels)
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
        val labels = store.labels.test()

        store.accept(Intent.Delete)

        assertContentEquals(listOf(Label.Deleted(id = itemId)), labels)
    }

    private fun createStore() {
        store =
            DetailsStoreFactory(
                storeFactory = DefaultStoreFactory(),
                database = database,
                mainContext = Dispatchers.Unconfined,
                ioContext = Dispatchers.Unconfined,
                itemId = itemId,
            ).create()
    }
}
