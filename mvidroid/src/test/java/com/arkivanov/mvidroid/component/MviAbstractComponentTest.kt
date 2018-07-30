package com.arkivanov.mvidroid.component

import com.arkivanov.mvidroid.store.MviStore
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MviAbstractComponentTest {

    private val storeLabels = PublishRelay.create<String>()
    private val store = mock<MviStore<String, String, String>> {
        on { labels }.thenReturn(storeLabels)
    }
    private val labels = PublishRelay.create<Any>()
    private var receivedLabels = TestObserver<Any>().also(labels::subscribe)

    @Test
    fun `isDisposed=true WHEN component is disposed`() {
        val component = TestComponent()
        component.dispose()
        assertTrue(component.isDisposed)
    }

    @Test
    fun `onDisposeAction is called WHEN component is disposed`() {
        val onDisposeAction = mock<() -> Unit>()
        TestComponent(onDisposeAction = onDisposeAction).dispose()
        verify(onDisposeAction)()
    }

    @Test
    fun `store is disposed WHEN not persistent AND component is disposed`() {
        TestComponent().dispose()
        verify(store).dispose()
    }

    @Test
    fun `store is not disposed WHEN persistent AND component is disposed`() {
        TestComponent(isPersistent = true).dispose()
        verify(store, never()).dispose()
    }

    @Test
    fun `intent received by store WHEN event transformer provided AND event published`() {
        val transformer: (String) -> String = mock {
            on { invoke("event") }.thenReturn("intent")
        }
        TestComponent(eventTransformer = transformer)("event")
        verify(store)("intent")
    }

    @Test
    fun `intent received by store WHEN label transformer provided AND label published`() {
        val transformer: (Any) -> String = mock {
            on { invoke("label") }.thenReturn("intent")
        }
        TestComponent(labelTransformer = transformer)
        labels.accept("label")
        verify(store)("intent")
    }

    @Test
    fun `label emitted WHEN store produced label AND store is not persistent`() {
        TestComponent()
        storeLabels.accept("label")
        assertEquals(1, receivedLabels.valueCount())
        assertEquals("label", receivedLabels.values()[0])
    }

    @Test
    fun `label emitted WHEN store produced label AND store persistent`() {
        TestComponent(isPersistent = true)
        storeLabels.accept("label")
        assertEquals(0, receivedLabels.valueCount())
    }

    private inner class TestComponent(
        eventTransformer: ((String) -> String?)? = null,
        labelTransformer: ((Any) -> String?)? = null,
        isPersistent: Boolean = false,
        onDisposeAction: (() -> Unit)? = null
    ) : MviAbstractComponent<String, String>(
        listOf(MviStoreBundle(store, eventTransformer, labelTransformer, isPersistent)),
        labels,
        onDisposeAction
    ) {
        override val states: String
            get() = throw UnsupportedOperationException()
    }
}
