package com.arkivanov.mvikotlin.extensions.coroutines

import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalMviKotlinApi::class)
@Suppress("TestFunctionName")
class CoroutineBootstrapperDslTest {

    @Test
    fun WHEN_bootstrapper_invoked_THEN_handler_invoked() {
        var isCalled = false
        val bootstrapper = coroutineBootstrapper<Nothing> { isCalled = true }

        bootstrapper()

        assertTrue(isCalled)
    }

    @Test
    fun GIVEN_launch_in_handler_WHEN_bootstrapper_disposed_THEN_job_disposed() {
        var job: Job? = null
        val bootstrapper = coroutineBootstrapper<Nothing>(Dispatchers.Unconfined) { job = launch { suspendCoroutine {} } }
        bootstrapper()

        bootstrapper.dispose()

        assertTrue(job?.isCancelled ?: false)
    }

    @Test
    fun WHEN_dispatch_from_handler_THEN_action_dispatcher() {
        val bootstrapper = coroutineBootstrapper<String> { dispatch("action") }
        var dispatchedAction: String? = null
        bootstrapper.init { dispatchedAction = it }

        bootstrapper()

        assertEquals("action", dispatchedAction)
    }
}
