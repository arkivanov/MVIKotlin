package com.arkivanov.mvikotlin.core.test.internal

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.utils.internal.assertOnMainThread
import com.arkivanov.mvikotlin.utils.internal.runOnBackgroundBlocking
import kotlin.test.Test

@Suppress("FunctionName", "UnnecessaryAbstractClass")
abstract class StoreThreadingTests(
    private val storeFactory: (
        initialState: String,
        bootstrapper: Bootstrapper<String>?,
        executorFactory: () -> Executor<String, String, String, String, String>,
        reducer: Reducer<String, String>
    ) -> Store<String, String, String>
) {

    @Test
    fun GIVEN_store_created_on_background_thread_WHEN_init_on_main_thread_THEN_no_crash() {
        assertOnMainThread()

        val store = runOnBackgroundBlocking { store() }
        store.init()
    }

    private fun store(
        initialState: String = "initial_state",
        bootstrapper: Bootstrapper<String>? = null,
        executorFactory: () -> Executor<String, String, String, String, String> = { TestExecutor() },
        reducer: Reducer<String, String> = reducer()
    ): Store<String, String, String> =
        storeFactory(initialState, bootstrapper, executorFactory, reducer)
}
