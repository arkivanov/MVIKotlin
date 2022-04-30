package com.arkivanov.mvikotlin.extensions.reaktive

import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalMviKotlinApi::class)
@Suppress("TestFunctionName")
class ReaktiveBootstrapperDslTest {

    @Test
    fun WHEN_bootstrapper_invoked_THEN_handler_invoked() {
        var isCalled = false
        val bootstrapper = reaktiveBootstrapper<Nothing> { isCalled = true }

        bootstrapper()

        assertTrue(isCalled)
    }

    @Test
    fun GIVEN_subscribeScoped_in_handler_WHEN_bootstrapper_disposed_THEN_subscription_disposed() {
        val observable = TestObservable<String>()
        val bootstrapper = reaktiveBootstrapper<Nothing> { observable.subscribeScoped() }
        bootstrapper()

        bootstrapper.dispose()

        assertFalse(observable.hasSubscribers)
    }

    @Test
    fun WHEN_dispatch_from_handler_THEN_action_dispatcher() {
        val bootstrapper = reaktiveBootstrapper<String> { dispatch("action") }
        var dispatchedAction: String? = null
        bootstrapper.init { dispatchedAction = it }

        bootstrapper()

        assertEquals("action", dispatchedAction)
    }
}
