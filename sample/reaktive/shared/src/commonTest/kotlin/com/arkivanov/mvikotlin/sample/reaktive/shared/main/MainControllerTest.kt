package com.arkivanov.mvikotlin.sample.reaktive.shared.main

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.TestDatabase
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.reaktive.shared.main.MainView.Model
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class MainControllerTest {

    private val storeFactory: StoreFactory = DefaultStoreFactory()
    private val database = TestDatabase()
    private val instanceKeeper: InstanceKeeper = InstanceKeeperDispatcher()

    private val view = TestMainView()

    @BeforeTest
    fun before() {
        overrideSchedulers(main = ::TestScheduler, io = { TestScheduler() })
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
    fun shows_items_WHEN_created() {
        val items = List(100) { database.create(TodoItem.Data(text = "text1", isDone = it % 2 == 0)) }.toModelItems()

        createController()

        assertContentEquals(items, view.model.items)
    }

    @Test
    fun adds_to_database_WHEN_Event_AddClicked() {
        createController()

        view.dispatch(Event.TextChanged(text = "text1"))
        view.dispatch(Event.AddClicked)
        view.dispatch(Event.TextChanged(text = "text2"))
        view.dispatch(Event.AddClicked)

        assertContentEquals(
            listOf(TodoItem.Data(text = "text1", isDone = false), TodoItem.Data(text = "text2", isDone = false)),
            database.getAll().map(TodoItem::data)
        )
    }

    @Test
    fun shows_added_items_WHEN_Event_AddClicked() {
        createController()

        view.dispatch(Event.TextChanged(text = "text1"))
        view.dispatch(Event.AddClicked)
        view.dispatch(Event.TextChanged(text = "text2"))
        view.dispatch(Event.AddClicked)

        assertContentEquals(database.getAll().toModelItems(), view.model.items)
    }

    @Test
    fun shows_updated_isDone_WHEN_Event_ItemDoneClicked() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2", isDone = true))
        val item3 = database.create(TodoItem.Data(text = "text3"))
        createController()

        view.dispatch(Event.ItemDoneClicked(id = item1.id))
        view.dispatch(Event.ItemDoneClicked(id = item2.id))
        view.dispatch(Event.ItemDoneClicked(id = item3.id))

        assertContentEquals(
            listOf(
                item1.copy(data = item1.data.copy(isDone = true)),
                item2.copy(data = item2.data.copy(isDone = false)),
                item3.copy(data = item3.data.copy(isDone = true))
            ).toModelItems(),
            view.model.items
        )
    }

    @Test
    fun does_not_show_deleted_items_WHEN_Event_ItemDeleteClicked() {
        database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        database.create(TodoItem.Data(text = "text3"))
        createController()

        view.dispatch(Event.ItemDeleteClicked(id = item2.id))

        assertFalse(view.model.items.any { it.id == item2.id })
    }

    @Test
    fun shows_changed_items_WHEN_Input_ItemChanged() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        val newItem1 = item1.copy(data = item1.data.copy(text = "text22", isDone = true))
        val controller = createController()

        controller.onItemChanged(id = item1.id, data = newItem1.data)

        assertContentEquals(listOf(newItem1, item2).toModelItems(), view.model.items)
    }

    @Test
    fun does_not_show_deleted_items_WHEN_Input_ItemDeleted() {
        val item1 = database.create(TodoItem.Data(text = "text1"))
        val item2 = database.create(TodoItem.Data(text = "text2"))
        val item3 = database.create(TodoItem.Data(text = "text3"))
        val controller = createController()

        controller.onItemDeleted(id = item2.id)

        assertContentEquals(listOf(item1, item3).toModelItems(), view.model.items)
    }

    @Test
    fun calls_onItemSelected_WHEN_Event_ItemClicked() {
        val item = database.create(TodoItem.Data(text = "text"))
        var selectedId: String? = null
        createController(onItemSelected = { selectedId = it })

        view.dispatch(Event.ItemClicked(id = item.id))

        assertEquals(item.id, selectedId)
    }

    private fun TodoItem.toModelItem(): Model.Item =
        Model.Item(
            id = id,
            text = data.text,
            isDone = data.isDone,
        )

    private fun List<TodoItem>.toModelItems(): List<Model.Item> =
        map { it.toModelItem() }

    private fun createController(onItemSelected: (id: String) -> Unit = {}): MainController {
        val lifecycle = LifecycleRegistry()

        val controller =
            MainController(
                storeFactory = storeFactory,
                database = database,
                lifecycle = lifecycle,
                instanceKeeper = instanceKeeper,
                onItemSelected = onItemSelected,
            )

        controller.onViewCreated(view, lifecycle)
        lifecycle.resume()

        return controller
    }
}
