package com.arkivanov.mvidroid.bind

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class MviLifecycleObserverAttachToTest {

    private lateinit var addedObserver: DefaultLifecycleObserver
    private val lifecycle = mock<Lifecycle> {
        on { addObserver(any()) }.thenAnswer { invocation ->
            addedObserver = invocation.getArgument(0)
            null
        }
    }

    private val owner = mock<LifecycleOwner>()
    private val observer = mock<MviLifecycleObserver>()

    // START_STOP strategy

    @Test
    fun `does not call onStart WHEN onCreate event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onCreate(owner)
        verify(observer, never()).onStart()
    }

    @Test
    fun `calls onStart WHEN onStart event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onStart(owner)
        verify(observer).onStart()
    }

    @Test
    fun `does not call onStart WHEN onResume event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onResume(owner)
        verify(observer, never()).onStart()
    }

    @Test
    fun `does not call onStop WHEN onPause event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onPause(owner)
        verify(observer, never()).onStop()
    }

    @Test
    fun `calls onStop WHEN onStop event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onStop(owner)
        verify(observer).onStop()
    }

    @Test
    fun `does not call onStop WHEN onDestroy event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onDestroy(owner)
        verify(observer, never()).onStop()
    }

    @Test
    fun `calls onDestroy WHEN onDestroy event and START_STOP strategy`() {
        attach(ArchLifecycleAttachStrategy.START_STOP)
        addedObserver.onDestroy(owner)
        verify(observer).onDestroy()
    }

    // RESUME_PAUSE strategy

    @Test
    fun `does not call onStart WHEN onCreate event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onCreate(owner)
        verify(observer, never()).onStart()
    }

    @Test
    fun `does not call onStart WHEN onStart event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onStart(owner)
        verify(observer, never()).onStart()
    }

    @Test
    fun `calls onStart WHEN onResume event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onResume(owner)
        verify(observer).onStart()
    }

    @Test
    fun `calls onStop WHEN onPause event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onPause(owner)
        verify(observer).onStop()
    }

    @Test
    fun `does not call onStop WHEN onStop event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onStop(owner)
        verify(observer, never()).onStop()
    }

    @Test
    fun `does not call onStop WHEN onDestroy event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onDestroy(owner)
        verify(observer, never()).onStop()
    }

    @Test
    fun `calls onDestroy WHEN onDestroy event and RESUME_PAUSE strategy`() {
        attach(ArchLifecycleAttachStrategy.RESUME_PAUSE)
        addedObserver.onDestroy(owner)
        verify(observer).onDestroy()
    }

    // CREATE_DESTROY strategy

    @Test
    fun `calls onStart WHEN onCreate event and CREATE_DESTROY strategy`() {
        attach(ArchLifecycleAttachStrategy.CREATE_DESTROY)
        addedObserver.onCreate(owner)
        verify(observer).onStart()
    }

    @Test
    fun `does not call onStart WHEN onStart event and CREATE_DESTROY strategy`() {
        attach(ArchLifecycleAttachStrategy.CREATE_DESTROY)
        addedObserver.onStart(owner)
        verify(observer, never()).onStart()
    }

    @Test
    fun `does not call onStart WHEN onResume event and CREATE_DESTROY strategy`() {
        attach(ArchLifecycleAttachStrategy.CREATE_DESTROY)
        addedObserver.onResume(owner)
        verify(observer, never()).onStart()
    }

    @Test
    fun `does not call onStop WHEN onPause event and CREATE_DESTROY strategy`() {
        attach(ArchLifecycleAttachStrategy.CREATE_DESTROY)
        addedObserver.onPause(owner)
        verify(observer, never()).onStop()
    }

    @Test
    fun `does not call onStop WHEN onStop event and CREATE_DESTROY strategy`() {
        attach(ArchLifecycleAttachStrategy.CREATE_DESTROY)
        addedObserver.onStop(owner)
        verify(observer, never()).onStop()
    }

    @Test
    fun `calls onDestroy after onStop WHEN onDestroy event and CREATE_DESTROY strategy`() {
        attach(ArchLifecycleAttachStrategy.CREATE_DESTROY)
        addedObserver.onDestroy(owner)
        val inOrder = inOrder(observer)
        inOrder.verify(observer).onStop()
        inOrder.verify(observer).onDestroy()
    }

    private fun attach(strategy: ArchLifecycleAttachStrategy) {
        observer.attachTo(lifecycle, strategy)
    }
}
