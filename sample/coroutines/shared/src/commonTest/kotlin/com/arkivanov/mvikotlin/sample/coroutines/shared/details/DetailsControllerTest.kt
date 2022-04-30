package com.arkivanov.mvikotlin.sample.coroutines.shared.details

import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.TestDatabase
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.database.TodoItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DetailsControllerTest {

    private val storeFactory: StoreFactory = DefaultStoreFactory()
    private val database = TestDatabase()

    private val itemData = TodoItem.Data(text = "text", isDone = true)
    private var itemId: String = database.create(itemData).id

    private val view = TestDetailsView()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
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
    fun shows_changed_text_WHEN_onTextChanged() {
        createController()

        view.dispatch(Event.TextChanged(text = "new_text"))

        assertEquals("new_text", view.model.text)
    }

    @Test
    fun calls_onItemChanged_WHEN_onTextChanged() {
        var changedItem: Pair<String, TodoItem.Data>? = null
        createController(onItemChanged = { id, data -> changedItem = id to data })

        view.dispatch(Event.TextChanged(text = "new_text"))

        assertEquals(itemId to itemData.copy(text = "new_text"), changedItem)
    }

    @Test
    fun shows_toggled_isDone_WHEN_onDoneClicked() {
        createController()

        view.dispatch(Event.DoneClicked)

        assertEquals(!itemData.isDone, view.model.isDone)
    }

    @Test
    fun calls_onItemChanged_WHEN_onDoneClicked() {
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
                dispatchers = object : TodoDispatchers {
                    override val main: CoroutineDispatcher get() = Dispatchers.Unconfined
                    override val io: CoroutineDispatcher get() = Dispatchers.Unconfined
                    override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
                },
                itemId = itemId,
                onItemChanged = onItemChanged,
                onItemDeleted = onItemDeleted,
            )

        controller.onViewCreated(view, lifecycle)
        lifecycle.resume()

        return controller
    }
}
