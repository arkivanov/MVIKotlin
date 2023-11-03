package com.arkivanov.mvikotlin.sample.reaktive.shared.details

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.database.MemoryTodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import com.arkivanov.mvikotlin.sample.reaktive.shared.details.DetailsView.Event
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DetailsControllerTest {

    private val storeFactory: StoreFactory = DefaultStoreFactory()
    private val database = MemoryTodoDatabase()

    private val itemData = TodoItem.Data(text = "text", isDone = true)
    private var itemId: String = database.create(itemData).id

    private val view = TestDetailsView()

    @BeforeTest
    fun before() {
        overrideSchedulers(main = ::TestScheduler, io = ::TestScheduler)
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

        assertEquals(itemData.text, view.model.text)
    }

    @Test
    fun shows_isDone_WHEN_created() {
        createController()

        assertEquals(itemData.isDone, view.model.isDone)
    }

    @Test
    fun shows_changed_text_WHEN_Event_TextChanged() {
        createController()

        view.dispatch(Event.TextChanged(text = "new_text"))

        assertEquals("new_text", view.model.text)
    }

    @Test
    fun calls_onItemChanged_WHEN_Event_TextChanged() {
        var changedItem: Pair<String, TodoItem.Data>? = null
        createController(onItemChanged = { id, data -> changedItem = id to data })

        view.dispatch(Event.TextChanged(text = "new_text"))

        assertEquals(itemId to itemData.copy(text = "new_text"), changedItem)
    }

    @Test
    fun shows_toggled_isDone_WHEN_Event_DoneClicked() {
        createController()

        view.dispatch(Event.DoneClicked)

        assertEquals(!itemData.isDone, view.model.isDone)
    }

    @Test
    fun calls_onItemChanged_WHEN_Event_DoneClicked() {
        var changedItem: Pair<String, TodoItem.Data>? = null
        createController(onItemChanged = { id, data -> changedItem = id to data })

        view.dispatch(Event.DoneClicked)

        assertEquals(itemId to itemData.copy(isDone = !itemData.isDone), changedItem)
    }

    @Test
    fun deletes_item_in_database_WHEN_onDeleteClicked() {
        createController()

        view.dispatch(Event.DeleteClicked)

        assertNull(database.get(id = itemId))
    }

    @Test
    fun calls_onItemDeleted_WHEN_onDeleteClicked() {
        var deletedId: String? = null
        createController(onItemDeleted = { deletedId = it })

        view.dispatch(Event.DeleteClicked)

        assertEquals(itemId, deletedId)
    }

    private fun createController(
        onItemChanged: (id: String, data: TodoItem.Data) -> Unit = { _, _ -> },
        onItemDeleted: (id: String) -> Unit = {},
    ): DetailsController {
        val lifecycle = LifecycleRegistry()

        val controller =
            DetailsController(
                storeFactory = storeFactory,
                database = database,
                lifecycle = lifecycle,
                itemId = itemId,
                onItemChanged = onItemChanged,
                onItemDeleted = onItemDeleted,
            )

        controller.onViewCreated(view, lifecycle)
        lifecycle.resume()

        return controller
    }
}
