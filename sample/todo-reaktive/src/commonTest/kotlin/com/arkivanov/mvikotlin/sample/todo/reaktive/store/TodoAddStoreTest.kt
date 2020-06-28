package com.arkivanov.mvikotlin.sample.todo.reaktive.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Intent
import com.arkivanov.mvikotlin.sample.todo.common.internal.store.add.TodoAddStore.Label
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

class TodoAddStoreTest {

    private val database = TestDatabase()

    private lateinit var store: TodoAddStore

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
        val labels = store.labels.test(autoFreeze = false)

        store.accept(Intent.Add)

        val createdItem = database.getAll().first()
        labels.assertValue(Label.Added(createdItem))
    }

    private fun createStore() {
        store = TodoAddStoreFactory(DefaultStoreFactory, database).create()
    }
}
