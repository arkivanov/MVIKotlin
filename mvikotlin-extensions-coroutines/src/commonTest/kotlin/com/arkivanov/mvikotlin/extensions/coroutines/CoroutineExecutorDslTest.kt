package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
@OptIn(ExperimentalMviKotlinApi::class)
class CoroutineExecutorDslTest {

    @Test
    fun GIVEN_onAction_with_final_class_WHEN_executeAction_with_same_final_class_THEN_onAction_called() {
        var some: Some.A? = null

        val executor =
            coroutineExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some.A> { some = it }
            }.invoke()

        executor.executeAction(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun GIVEN_onAction_with_final_class_WHEN_executeAction_with_another_final_class_THEN_onAction_not_called() {
        var some: Some.A? = null

        val executor =
            coroutineExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some.A> { some = it }
            }.invoke()

        executor.executeAction(Some.B)

        assertNull(some)
    }

    @Test
    fun GIVEN_onAction_with_common_interface_WHEN_executeAction_with_implementing_final_class_THEN_onAction_called() {
        var some: Some? = null

        val executor =
            coroutineExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some> { some = it }
            }.invoke()

        executor.executeAction(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun GIVEN_onIntent_with_final_class_WHEN_executeIntent_with_same_final_class_THEN_onIntent_called() {
        var some: Some.A? = null

        val executor =
            coroutineExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some.A> { some = it }
            }.invoke()

        executor.executeIntent(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun GIVEN_onIntent_with_final_class_WHEN_executeIntent_with_another_final_class_THEN_onIntent_not_called() {
        var some: Some.A? = null

        val executor =
            coroutineExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some.A> { some = it }
            }.invoke()

        executor.executeIntent(Some.B)

        assertNull(some)
    }

    @Test
    fun GIVEN_onIntent_with_common_interface_WHEN_executeIntent_with_implementing_final_class_THEN_onIntent_called() {
        var some: Some? = null

        val executor =
            coroutineExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some> { some = it }
            }.invoke()

        executor.executeIntent(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun WHEN_read_state_in_onIntent_THEN_returns_current_state() {
        var readState: String? = null

        val executor =
            coroutineExecutorFactory<Some, Nothing, String, Nothing, Nothing> {
                onIntent<Some.A> { readState = state }
            }.invoke()

        executor.init(
            object : Executor.Callbacks<String, Nothing, Nothing> {
                override val state: String = "state"

                override fun onMessage(message: Nothing) {
                    // no-op
                }

                override fun onLabel(label: Nothing) {
                    // no-op
                }
            }
        )

        executor.executeIntent(Some.A)

        assertEquals("state", readState)
    }

    @Test
    fun WHEN_read_state_in_onAction_THEN_returns_current_state() {
        var readState: String? = null

        val executor =
            coroutineExecutorFactory<Nothing, Some, String, Nothing, Nothing> {
                onAction<Some.A> { readState = state }
            }.invoke()

        executor.init(
            object : Executor.Callbacks<String, Nothing, Nothing> {
                override val state: String = "state"

                override fun onMessage(message: Nothing) {
                    // no-op
                }

                override fun onLabel(label: Nothing) {
                    // no-op
                }
            }
        )

        executor.executeAction(Some.A)

        assertEquals("state", readState)
    }

    @Test
    fun GIVEN_launch_in_onAction_WHEN_executor_disposed_THEN_job_cancelled() {
        var job: Job? = null

        val executor =
            coroutineExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing>(Dispatchers.Unconfined) {
                onAction<Some.A> { job = launch { suspendCoroutine {} } }
            }.invoke()

        executor.executeAction(Some.A)

        executor.dispose()

        assertTrue(job?.isCancelled ?: false)
    }

    @Test
    fun GIVEN_launch_in_onIntent_WHEN_executor_disposed_THEN_job_cancelled() {
        var job: Job? = null

        val executor =
            coroutineExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing>(Dispatchers.Unconfined) {
                onIntent<Some.A> { job = launch { suspendCoroutine {} } }
            }.invoke()

        executor.executeIntent(Some.A)

        executor.dispose()

        assertTrue(job?.isCancelled ?: false)
    }

    @Test
    fun WHEN_dispatch_from_onAction_THEN_message_dispatched() {
        val executor =
            coroutineExecutorFactory<Nothing, Some, Unit, String, Nothing> {
                onAction<Some.A> { dispatch("message") }
            }.invoke()

        var dispatchedMessage: String? = null

        executor.init(
            object : Executor.Callbacks<Unit, String, Nothing> {
                override val state: Unit = Unit

                override fun onMessage(message: String) {
                    dispatchedMessage = message
                }

                override fun onLabel(label: Nothing) {
                    // no-op
                }
            }
        )

        executor.executeAction(Some.A)

        assertEquals("message", dispatchedMessage)
    }

    @Test
    fun WHEN_dispatch_from_onIntent_THEN_message_dispatched() {
        val executor =
            coroutineExecutorFactory<Some, Nothing, Unit, String, Nothing> {
                onIntent<Some.A> { dispatch("message") }
            }.invoke()

        var dispatchedMessage: String? = null

        executor.init(
            object : Executor.Callbacks<Unit, String, Nothing> {
                override val state: Unit = Unit

                override fun onMessage(message: String) {
                    dispatchedMessage = message
                }

                override fun onLabel(label: Nothing) {
                    // no-op
                }
            }
        )

        executor.executeIntent(Some.A)

        assertEquals("message", dispatchedMessage)
    }

    @Test
    fun WHEN_publish_from_onAction_THEN_label_published() {
        val executor =
            coroutineExecutorFactory<Nothing, Some, Unit, Nothing, String> {
                onAction<Some.A> { publish("label") }
            }.invoke()

        var dispatchedLabel: String? = null

        executor.init(
            object : Executor.Callbacks<Unit, Nothing, String> {
                override val state: Unit = Unit

                override fun onMessage(message: Nothing) {
                    // no-op
                }

                override fun onLabel(label: String) {
                    dispatchedLabel = label
                }
            }
        )

        executor.executeAction(Some.A)

        assertEquals("label", dispatchedLabel)
    }


    @Test
    fun WHEN_publish_from_onIntent_THEN_label_published() {
        val executor =
            coroutineExecutorFactory<Some, Nothing, Unit, Nothing, String> {
                onIntent<Some.A> { publish("label") }
            }.invoke()

        var dispatchedLabel: String? = null

        executor.init(
            object : Executor.Callbacks<Unit, Nothing, String> {
                override val state: Unit = Unit

                override fun onMessage(message: Nothing) {
                    // no-op
                }

                override fun onLabel(label: String) {
                    dispatchedLabel = label
                }
            }
        )

        executor.executeIntent(Some.A)

        assertEquals("label", dispatchedLabel)
    }

    private sealed interface Some {
        object A : Some
        object B : Some
    }
}
