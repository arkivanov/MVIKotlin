package com.arkivanov.mvidroid.store.factory

import com.arkivanov.mvidroid.store.component.MviExecutor
import com.arkivanov.mvidroid.store.component.MviReducer
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertEquals
import org.junit.Test

class MviDefaultStoreFactoryTest {

    @Test
    fun `can create with reducer`() {
        MviDefaultStoreFactory.create<String, String, String, String, String>("state", mock(), mock(), mock(), mock())
    }

    @Test
    fun `can create without reducer`() {
        MviDefaultStoreFactory.create<String, String, String, String, String>("state", mock(), mock(), mock(), null)
    }

    @Test
    fun `can create intentless`() {
        MviDefaultStoreFactory.createIntentless<String, String, String, String>("state", mock(), mock(), mock())
    }

    @Test
    fun `can create actionless`() {
        val executor = object : MviExecutor<String, String, String, String>() {
            override fun invoke(action: String): Disposable? {
                if (action == "intent") {
                    dispatch("result")
                }
                return null
            }
        }
        val reducer = object : MviReducer<String, String> {
            override fun String.reduce(result: String): String =
                if (result == "result") "state" else this
        }
        val store = MviDefaultStoreFactory.createActionless("", executor, reducer)
        store("intent")
        assertEquals("state", store.state)
    }

    @Test
    fun `can create executorless`() {
        val reducer = object : MviReducer<String, String> {
            override fun String.reduce(result: String): String =
                if (result == "intent") "state" else this
        }
        val store = MviDefaultStoreFactory.createExecutorless<String, String, String>("", reducer)
        store.invoke("intent")
        assertEquals("state", store.state)
    }
}
