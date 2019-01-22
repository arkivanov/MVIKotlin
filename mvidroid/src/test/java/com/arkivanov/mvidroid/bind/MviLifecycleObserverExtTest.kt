package com.arkivanov.mvidroid.bind

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class MviLifecycleObserverExtTest {

    private lateinit var addedObserver: DefaultLifecycleObserver
    private val lifecycle = mock<Lifecycle> {
        on { addObserver(any()) }.thenAnswer { invocation ->
            addedObserver = invocation.getArgument(0)
            null
        }
    }

    private val owner = mock<LifecycleOwner>()
    private val observer = mock<MviLifecycleObserver>()

    init {
        observer.attachTo(lifecycle)
    }

    @Test
    fun `calls onStart WHEN onStart event`() {
        addedObserver.onStart(owner)
        verify(observer).onStart()
    }

    @Test
    fun `calls onStop WHEN onStop event`() {
        addedObserver.onStop(owner)
        verify(observer).onStop()
    }

    @Test
    fun `calls onDestroy WHEN onDestroy event`() {
        addedObserver.onDestroy(owner)
        verify(observer).onDestroy()
    }
}
