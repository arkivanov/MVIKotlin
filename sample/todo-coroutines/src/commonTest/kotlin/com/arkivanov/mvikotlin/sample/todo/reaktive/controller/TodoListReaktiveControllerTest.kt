package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.keepers.instancekeeper.DefaultInstanceKeeper
import com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Input
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController.Output
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoListCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.TestDatabase
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TodoListReaktiveControllerTest {

    private val lifecycle = LifecycleRegistry()
    private val database = TestDatabase()
    private val output = ArrayList<Output>()

    private val dependencies =
        object : Dependencies {
            override val storeFactory: StoreFactory = DefaultStoreFactory
            override val database: TodoDatabase = this@TodoListReaktiveControllerTest.database
            override val lifecycle: Lifecycle = this@TodoListReaktiveControllerTest.lifecycle
            override val instanceKeeper: InstanceKeeper = DefaultInstanceKeeper()
            override val listOutput: (Output) -> Unit = { output += it }
        }

    private val listView = TestListView()
    private val addView = TestAddView()
    private lateinit var controller: TodoListController

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun shows_items_WHEN_created() {
        val items = List(100) { database.create(TodoItem.Data(text = "text1", isDone = it % 2 == 0)) }

        createController()

        assertEquals(items, listView.model.items)
    }

    @Test
    fun shows_added_items_WHEN_Event_AddClicked() {
        createController()

        addView.dispatch(TodoAddView.Event.TextChanged(text = "text1"))
        addView.dispatch(TodoAddView.Event.AddClicked)
        addView.dispatch(TodoAddView.Event.TextChanged(text = "text2"))
        addView.dispatch(TodoAddView.Event.AddClicked)

        val expectedData = listOf(TodoItem.Data(text = "text1", isDone = false), TodoItem.Data(text = "text2", isDone = false))
        val displayedData = listView.model.items.map(TodoItem::data)
        assertEquals(expectedData, displayedData)
    }

    @Test
    fun shows_updated_isDone_WHEN_Event_ItemDoneClicked() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2", isDone = true))
        val item3 = database.create(TodoItem.Data(text = "text3"))
        createController()

        listView.dispatch(TodoListView.Event.ItemDoneClicked(id = item1.id))
        listView.dispatch(TodoListView.Event.ItemDoneClicked(id = item2.id))
        listView.dispatch(TodoListView.Event.ItemDoneClicked(id = item3.id))

        assertEquals(
            listOf(
                item1.copy(data = item1.data.copy(isDone = true)),
                item2.copy(data = item2.data.copy(isDone = false)),
                item3.copy(data = item3.data.copy(isDone = true))

            ),
            listView.model.items
        )
    }

    @Test
    fun does_not_show_deleted_items_WHEN_Event_ItemDeleteClicked() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        val item3 = database.create(TodoItem.Data(text = "text3"))
        createController()

        listView.dispatch(TodoListView.Event.ItemDeleteClicked(id = item2.id))

        assertEquals(listOf(item1, item3), listView.model.items)
    }

    @Test
    fun shows_changed_items_WHEN_Input_ItemChanged() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        val newItem1 = item1.copy(data = item1.data.copy(text = "text22", isDone = true))
        createController()

        controller.input(Input.ItemChanged(id = item1.id, data = newItem1.data))

        assertEquals(listOf(newItem1, item2), listView.model.items)
    }

    @Test
    fun does_not_show_deleted_items_WHEN_Input_ItemDeleted() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        val item3 = database.create(TodoItem.Data(text = "text3"))
        createController()

        controller.input(Input.ItemDeleted(id = item2.id))

        assertEquals(listOf(item1, item3), listView.model.items)
    }

    @Test
    fun publishes_Output_ItemSelected_WHEN_Event_ItemClicked() {
        val item = database.create(TodoItem.Data(text = "text"))
        createController()

        listView.dispatch(TodoListView.Event.ItemClicked(id = item.id))

        assertTrue(Output.ItemSelected(id = item.id) in output)
    }

    private fun createController() {
        controller =
            TodoListCoroutinesController(
                dependencies = dependencies,
                mainContext = Dispatchers.Unconfined,
                ioContext = Dispatchers.Unconfined
            )
        controller.onViewCreated(listView, addView, lifecycle)
        lifecycle.resume()
    }
}
