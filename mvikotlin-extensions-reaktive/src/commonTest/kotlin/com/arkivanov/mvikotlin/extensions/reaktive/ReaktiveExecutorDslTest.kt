package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.test.internal.DefaultExecutorCallbacks
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

@Suppress("TestFunctionName")
@OptIn(ExperimentalMviKotlinApi::class)
class ReaktiveExecutorDslTest {

    @Test
    fun GIVEN_onAction_with_final_class_WHEN_executeAction_with_same_final_class_THEN_onAction_called() {
        var some: Some.A? = null

        val executor =
            reaktiveExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some.A> { some = it }
            }.invoke()

        executor.executeAction(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun GIVEN_onAction_with_final_class_WHEN_executeAction_with_another_final_class_THEN_onAction_not_called() {
        var some: Some.A? = null

        val executor =
            reaktiveExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some.A> { some = it }
            }.invoke()

        executor.executeAction(Some.B)

        assertNull(some)
    }

    @Test
    fun GIVEN_onAction_with_common_interface_WHEN_executeAction_with_implementing_final_class_THEN_onAction_called() {
        var some: Some? = null

        val executor =
            reaktiveExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some> { some = it }
            }.invoke()

        executor.executeAction(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun GIVEN_onIntent_with_final_class_WHEN_executeIntent_with_same_final_class_THEN_onIntent_called() {
        var some: Some.A? = null

        val executor =
            reaktiveExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some.A> { some = it }
            }.invoke()

        executor.executeIntent(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun GIVEN_onIntent_with_final_class_WHEN_executeIntent_with_another_final_class_THEN_onIntent_not_called() {
        var some: Some.A? = null

        val executor =
            reaktiveExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some.A> { some = it }
            }.invoke()

        executor.executeIntent(Some.B)

        assertNull(some)
    }

    @Test
    fun GIVEN_onIntent_with_common_interface_WHEN_executeIntent_with_implementing_final_class_THEN_onIntent_called() {
        var some: Some? = null

        val executor =
            reaktiveExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some> { some = it }
            }.invoke()

        executor.executeIntent(Some.A)

        assertEquals(Some.A, some)
    }

    @Test
    fun WHEN_read_state_in_onIntent_THEN_returns_current_state() {
        var readState: String? = null

        val executor =
            reaktiveExecutorFactory<Some, Nothing, String, Nothing, Nothing> {
                onIntent<Some.A> { readState = state }
            }.invoke()

        executor.init(
            object : Executor.Callbacks<String, Nothing, Nothing, Nothing> {
                override val state: String = "state"

                override fun onMessage(message: Nothing) {
                    // no-op
                }

                override fun onAction(action: Nothing) {
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
            reaktiveExecutorFactory<Nothing, Some, String, Nothing, Nothing> {
                onAction<Some.A> { readState = state }
            }.invoke()

        executor.init(
            object : DefaultExecutorCallbacks<String, Any, Any, Any> {
                override val state: String = "state"
            }
        )

        executor.executeAction(Some.A)

        assertEquals("state", readState)
    }

    @Test
    fun GIVEN_subscribeScoped_in_onAction_WHEN_executor_disposed_THEN_subscription_disposed() {
        val observable = TestObservable<String>()

        val executor =
            reaktiveExecutorFactory<Nothing, Some, Nothing, Nothing, Nothing> {
                onAction<Some.A> { observable.subscribeScoped() }
            }.invoke()

        executor.executeAction(Some.A)

        executor.dispose()

        assertFalse(observable.hasSubscribers)
    }

    @Test
    fun GIVEN_subscribeScoped_in_onIntent_WHEN_executor_disposed_THEN_subscription_disposed() {
        val observable = TestObservable<String>()

        val executor =
            reaktiveExecutorFactory<Some, Nothing, Nothing, Nothing, Nothing> {
                onIntent<Some.A> { observable.subscribeScoped() }
            }.invoke()

        executor.executeIntent(Some.A)

        executor.dispose()

        assertFalse(observable.hasSubscribers)
    }

    @Test
    fun WHEN_dispatch_from_onAction_THEN_message_dispatched() {
        val executor =
            reaktiveExecutorFactory<Nothing, Some, Unit, String, Nothing> {
                onAction<Some.A> { dispatch("message") }
            }.invoke()

        var dispatchedMessage: String? = null

        executor.init(
            object : DefaultExecutorCallbacks<Unit, String, Any, Any> {
                override val state: Unit = Unit

                override fun onMessage(message: String) {
                    dispatchedMessage = message
                }
            }
        )

        executor.executeAction(Some.A)

        assertEquals("message", dispatchedMessage)
    }

    @Test
    fun WHEN_dispatch_from_onIntent_THEN_message_dispatched() {
        val executor =
            reaktiveExecutorFactory<Some, Nothing, Unit, String, Nothing> {
                onIntent<Some.A> { dispatch("message") }
            }.invoke()

        var dispatchedMessage: String? = null

        executor.init(
            object : DefaultExecutorCallbacks<Unit, String, Any, Any> {
                override val state: Unit = Unit

                override fun onMessage(message: String) {
                    dispatchedMessage = message
                }
            }
        )

        executor.executeIntent(Some.A)

        assertEquals("message", dispatchedMessage)
    }

    @Test
    fun WHEN_forward_from_onAction_THEN_action_forwarded() {
        val executor =
            reaktiveExecutorFactory<Nothing, Some, Unit, String, Nothing> {
                onAction<Some.A> { forward(Some.B) }
            }.invoke()

        var forwardedAction: Some? = null

        executor.init(
            object : DefaultExecutorCallbacks<Unit, Any, Some, Any> {
                override val state: Unit = Unit

                override fun onAction(action: Some) {
                    forwardedAction = action
                }
            }
        )

        executor.executeAction(Some.A)

        assertEquals(Some.B, forwardedAction)
    }

    @Test
    fun WHEN_forward_from_onIntent_THEN_action_forward() {
        val executor =
            reaktiveExecutorFactory<Some, String, Unit, String, Nothing> {
                onIntent<Some.A> { forward("action") }
            }.invoke()

        var forwardedAction: String? = null

        executor.init(
            object : DefaultExecutorCallbacks<Unit, Any, String, Any> {
                override val state: Unit = Unit

                override fun onAction(action: String) {
                    forwardedAction = action
                }
            }
        )

        executor.executeIntent(Some.A)

        assertEquals("action", forwardedAction)
    }

    @Test
    fun WHEN_publish_from_onAction_THEN_label_published() {
        val executor =
            reaktiveExecutorFactory<Nothing, Some, Unit, Nothing, String> {
                onAction<Some.A> { publish("label") }
            }.invoke()

        var dispatchedLabel: String? = null

        executor.init(
            object : DefaultExecutorCallbacks<Unit, Any, Any, String> {
                override val state: Unit = Unit

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
            reaktiveExecutorFactory<Some, Nothing, Unit, Nothing, String> {
                onIntent<Some.A> { publish("label") }
            }.invoke()

        var dispatchedLabel: String? = null

        executor.init(
            object : DefaultExecutorCallbacks<Unit, Any, Any, String> {
                override val state: Unit = Unit

                override fun onLabel(label: String) {
                    dispatchedLabel = label
                }
            }
        )

        executor.executeIntent(Some.A)

        assertEquals("label", dispatchedLabel)
    }

    private sealed interface Some {
        data object A : Some
        data object B : Some
    }
}
