package com.arkivanov.mvidroid.view

import com.nhaarman.mockito_kotlin.*
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MviBaseViewTest {

    private val consumer = mockConsumer()
    private val view = TestView()

    @Before
    fun before() {
        view.registerTestDiff()
    }

    @Test
    fun `first null value bound`() {
        view.bind(TestViewModel(null))
        verify(consumer).setValue(null)
    }

    @Test
    fun `first not null value bound`() {
        view.bind(TestViewModel("value"))
        verify(consumer).setValue("value")
    }

    @Test
    fun `second value bound WHEN not equals`() {
        view.bind(TestViewModel("value1"))
        clearInvocations(consumer)
        view.bind(TestViewModel("value2"))
        verify(consumer).setValue("value2")
    }

    @Test
    fun `second value not bound WHEN equals`() {
        view.bind(TestViewModel("value"))
        clearInvocations(consumer)
        view.bind(TestViewModel("value"))
        verify(consumer, never()).setValue(any())
    }

    @Test
    fun `third value bound WHEN tree models received with equal and not equal values`() {
        view.bind(TestViewModel("value1"))
        view.bind(TestViewModel("value1"))
        clearInvocations(consumer)
        view.bind(TestViewModel("value2"))
        verify(consumer).setValue("value2")
    }

    @Test
    fun `event emitted WHEN dispatched`() {
        val view = TestView()
        val observer = TestObserver<String>()
        view.events.subscribe(observer)
        view.dispatchTestEvent()
        assertEquals(1, observer.values().size)
        assertEquals("event", observer.values()[0])
    }

    private fun mockConsumer(): TestConsumer = mock()

    private interface TestConsumer {
        fun setValue(value: String?)
    }

    private data class TestViewModel(
        val value: String?
    )

    private inner class TestView : MviBaseView<TestViewModel, String>() {
        fun dispatchTestEvent() {
            dispatch("event")
        }

        fun registerTestDiff() {
            registerDiff(TestViewModel::value, { a, b -> a == b }, consumer::setValue)
        }
    }
}
