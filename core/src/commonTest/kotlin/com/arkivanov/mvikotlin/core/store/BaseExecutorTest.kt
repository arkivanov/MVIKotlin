package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.utils.internal.lazyAtomicReference
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseExecutorTest {

    private val executor = BaseExecutorImpl()

    @Test
    fun returns_state() {
        executor.init(callbacks(stateSupplier = { "state" }))

        assertEquals("state", executor.getState())
    }

    @Test
    fun dispatches_result() {
        val result = lazyAtomicReference<String>()

        executor.init(callbacks(onResult = { result.value = it }))
        executor.dispatchResult("result")

        assertEquals("result", result.value)
    }

    @Test
    fun publishes_label() {
        val label = lazyAtomicReference<String>()

        executor.init(callbacks(onLabel = { label.value = it }))
        executor.publishLabel("label")

        assertEquals("label", label.value)
    }

    private fun callbacks(
        stateSupplier: () -> String = { "state" },
        onResult: (String) -> Unit = {},
        onLabel: (String) -> Unit = {}
    ): Executor.Callbacks<String, String, String> =
        object : Executor.Callbacks<String, String, String> {
            override val state: String get() = stateSupplier()

            override fun onResult(result: String) {
                onResult.invoke(result)
            }

            override fun onLabel(label: String) {
                onLabel.invoke(label)
            }
        }

    private class BaseExecutorImpl : BaseExecutor<String, String, String, String, String>() {
        init {
            freeze()
        }

        fun getState(): String = state

        fun dispatchResult(result: String) {
            dispatch(result)
        }

        fun publishLabel(label: String) {
            publish(label)
        }
    }
}
