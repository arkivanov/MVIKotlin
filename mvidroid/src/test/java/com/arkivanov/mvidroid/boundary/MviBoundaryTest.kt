package com.arkivanov.mvidroid.boundary

import com.arkivanov.kfunction.KFunction
import com.arkivanov.mvidroid.store.MviStore
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MviBoundaryTest {

    private val storeLabels = PublishRelay.create<Any>()
    private val store: MviStore<String, String> = mock {
        on { labels }.thenReturn(storeLabels)
    }
    private val events = PublishRelay.create<String>()
    private val labels = PublishRelay.create<Any>()

    @Test
    fun `WHEN boundary is disposed THEN isDisposed=true`() {
        val boundary = TestBoundary()

        boundary.dispose()

        assertTrue(boundary.isDisposed)
    }

    @Test
    fun `WHEN store is not persistent AND boundary is disposed THEN store is disposed`() {
        val boundary = TestBoundary()

        boundary.dispose()

        verify(store).dispose()
    }

    @Test
    fun `WHEN store is persistent AND boundary is disposed THEN store is not disposed`() {
        val boundary = TestBoundary(isPersistent = true)

        boundary.dispose()

        verify(store, never()).dispose()
    }

    @Test
    fun `WHEN event transformer provided AND event published THEN intent received by store`() {
        val transformer: KFunction<String, String> = mock {
            on { invoke("event") }.thenReturn("intent")
        }
        TestBoundary(eventTransformer = transformer)

        events.accept("event")

        verify(store)("intent")
    }

    @Test
    fun `WHEN label transformer provided AND label published THEN intent received by store`() {
        val transformer: KFunction<Any, String> = mock {
            on { invoke("label") }.thenReturn("intent")
        }
        TestBoundary(labelTransformer = transformer)

        labels.accept("label")

        verify(store)("intent")
    }

    @Test
    fun `WHEN store produced label THEN label emitted`() {
        TestBoundary()
        val observer = TestObserver<Any>()
        labels.subscribe(observer)

        storeLabels.accept("label")

        assertEquals(1, observer.valueCount())
        assertEquals("label", observer.values()[0])
    }

    private inner class TestBoundary(
        eventTransformer: KFunction<String, String?>? = null,
        labelTransformer: KFunction<Any, String?>? = null,
        isPersistent: Boolean = false
    ) : MviBoundary<String>(listOf(StoreBundle(store, eventTransformer, labelTransformer, isPersistent)), events, labels)
}
