package com.arkivanov.mvikotlin.sample.todo.reaktive.controller

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Dependencies
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController.Output
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView.Event
import com.arkivanov.mvikotlin.sample.todo.reaktive.TestDatabase
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TodoDetailsReaktiveControllerTest {

    private lateinit var itemId: String
    private val itemData = TodoItem.Data(text = "text", isDone = true)
    private val lifecycle = LifecycleRegistry()
    private val database = TestDatabase()
    private val output = ArrayList<Output>()

    private val dependencies =
        object : Dependencies {
            override val storeFactory: StoreFactory = DefaultStoreFactory
            override val database: TodoDatabase = this@TodoDetailsReaktiveControllerTest.database
            override val lifecycle: Lifecycle = this@TodoDetailsReaktiveControllerTest.lifecycle
            override val itemId: String get() = this@TodoDetailsReaktiveControllerTest.itemId
            override val detailsOutput: (Output) -> Unit = { output += it }
        }

    private val detailsView = TestDetailsView()
    private lateinit var controller: TodoDetailsController

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
    fun shows_text_WHEN_created() {
        createController()

        assertEquals(itemData.text, detailsView.model.text)
    }

    @Test
    fun shows_isDone_WHEN_created() {
        createController()

        assertEquals(itemData.isDone, detailsView.model.isDone)
    }

    @Test
    fun shows_changed_text_WHEN_Event_TextChanged() {
        createController()

        detailsView.dispatch(Event.TextChanged(text = "new_text"))

        assertEquals("new_text", detailsView.model.text)
    }

    @Test
    fun publishes_Output_ItemChanged_WHEN_Event_TextChanged() {
        createController()

        detailsView.dispatch(Event.TextChanged(text = "new_text"))

        assertTrue(Output.ItemChanged(id = itemId, data = itemData.copy(text = "new_text")) in output)
    }

    @Test
    fun shows_toggled_isDone_WHEN_Event_DoneClicked() {
        createController()

        detailsView.dispatch(Event.DoneClicked)

        assertEquals(!itemData.isDone, detailsView.model.isDone)
    }

    @Test
    fun publishes_Output_ItemChanged_WHEN_Event_DoneClicked() {
        createController()

        detailsView.dispatch(Event.DoneClicked)

        assertTrue(Output.ItemChanged(id = itemId, data = itemData.copy(isDone = !itemData.isDone)) in output)
    }

    @Test
    fun deletes_item_in_database_WHEN_Event_DeleteClicked() {
        createController()

        detailsView.dispatch(Event.DeleteClicked)

        assertNull(database.get(id = itemId))
    }

    @Test
    fun publishes_Output_ItemDeleted_WHEN_Event_DeleteClicked() {
        createController()

        detailsView.dispatch(Event.DeleteClicked)

        assertTrue(Output.ItemDeleted(id = itemId) in output)
    }

    @Test
    fun publishes_Output_Finished_WHEN_Event_DeleteClicked() {
        createController()

        detailsView.dispatch(Event.DeleteClicked)

        assertTrue(Output.Finished in output)
    }

    private fun createController() {
        itemId = database.create(itemData).id
        controller = TodoDetailsReaktiveController(dependencies)
        controller.onViewCreated(detailsView, lifecycle)
        lifecycle.resume()
    }
}
