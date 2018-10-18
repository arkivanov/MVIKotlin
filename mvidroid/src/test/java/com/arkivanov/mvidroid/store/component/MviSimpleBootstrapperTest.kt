package com.arkivanov.mvidroid.store.component

import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test

class MviSimpleBootstrapperTest {

    @Test
    fun `dispatches all actions`() {
        val callback = mock<(String) -> Unit>()
        MviSimpleBootstrapper("action1", "action2").bootstrap(callback)
        inOrder(callback).apply {
            verify(callback).invoke("action1")
            verify(callback).invoke("action2")
        }
    }
}