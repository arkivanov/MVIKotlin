package com.arkivanov.mvidroid.store.component

import io.reactivex.disposables.Disposable
import org.junit.Assert.assertEquals
import org.junit.Test

class MviExecutorTest {

    private lateinit var str: String
    private val executor = MyTestExecutor()

    init {
        executor.init({ "state" }, { str = it }, { str = it })
    }

    @Test
    fun `can read state`() {
        assertEquals("state", executor.getState())
    }

    @Test
    fun `can dispatch result`() {
        executor.dispatchResult("result")
        assertEquals("result", str)
    }

    @Test
    fun `can publish label`() {
        executor.publishLabel("label")
        assertEquals("label", str)
    }

    private class MyTestExecutor : MviExecutor<String, String, String, String>() {
        override fun execute(action: String): Disposable? = null

        fun getState(): String = state

        fun dispatchResult(result: String) {
            dispatch(result)
        }

        fun publishLabel(result: String) {
            publish(result)
        }
    }
}
