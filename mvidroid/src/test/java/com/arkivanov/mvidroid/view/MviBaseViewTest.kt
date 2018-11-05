package com.arkivanov.mvidroid.view

import com.nhaarman.mockito_kotlin.*
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class MviBaseViewTest {

    private val consumer = mockConsumer()

    @Test
    fun `null value bound using comparator WHEN first model received`() {
        registerDiff({ null }, { _, _ -> false }).bind("value")
        verify(consumer).setValue(null)
    }

    @Test
    fun `second value bound using comparator WHEN not equals`() {
        val view = registerDiff(String::toString) { _, _ -> false }
        view.bind("value")
        clearInvocations(consumer)
        view.bind("value")
        verify(consumer).setValue("value")
    }

    @Test
    fun `second value not bound using comparator WHEN equals`() {
        val view = registerDiff(String::toString) { _, _ -> true }
        view.bind("value")
        clearInvocations(consumer)
        view.bind("value")
        verify(consumer, never()).setValue(any())
    }

    @Test
    fun `null value bound using equals WHEN first model received`() {
        registerDiffByEquals { null }.bind(createString("value"))
        verify(consumer).setValue(null)
    }

    @Test
    fun `second value bound using equals WHEN two model received with different values`() {
        val view = registerDiffByEquals(String::toString)
        view.bind(createString("value1"))
        clearInvocations(consumer)
        view.bind(createString("value2"))
        verify(consumer).setValue("value2")
    }

    @Test
    fun `only first value bound using equals WHEN two model received with equal values`() {
        val view = registerDiffByEquals(String::toString)
        view.bind(createString("value1"))
        view.bind(createString("value1"))
        verify(consumer).setValue("value1")
    }

    @Test
    fun `third value bound using equals WHEN tree models received with equal and not equal values`() {
        val view = registerDiffByEquals(String::toString)
        view.bind(createString("value1"))
        view.bind(createString("value1"))
        clearInvocations(consumer)
        view.bind(createString("value2"))
        verify(consumer).setValue("value2")
    }

    @Test
    fun `null value bound using reference WHEN first model received`() {
        registerDiffByReference { null }.bind("value")
        verify(consumer).setValue(null)
    }

    @Test
    fun `second value bound using reference WHEN two model received with different values`() {
        val view = registerDiffByReference(String::toString)
        view.bind(createString("value"))
        clearInvocations(consumer)
        view.bind(createString("value"))
        verify(consumer).setValue("value")
    }

    @Test
    fun `only first value bound using reference WHEN two model received with equal values`() {
        val view = registerDiffByReference(String::toString)
        view.bind("value")
        view.bind("value")
        verify(consumer).setValue("value")
    }

    @Test
    fun `third value bound using reference WHEN tree models received with equal and not equal values`() {
        val view = registerDiffByReference(String::toString)
        view.bind("value")
        view.bind("value")
        clearInvocations(consumer)
        view.bind(createString("value"))
        verify(consumer).setValue("value")
    }


    @Test
    fun `ui event emitted WHEN dispatched`() {
        val view = TestView()
        val observer = TestObserver<String>()
        view.events.subscribe(observer)
        view.dispatchEvent()
        assertEquals(1, observer.values().size)
        assertEquals("event", observer.values()[0])
    }

    private fun mockConsumer(): TestConsumer = mock()

    private fun registerDiff(getValue: String.() -> String?, comparator: (String?, String?) -> Boolean): MviBaseView<String, String> =
        object : MviBaseView<String, String>() {
            init {
                registerDiff(consumer, getValue, comparator, TestConsumer::setValue)
            }
        }

    private fun registerDiffByEquals(getValue: String.() -> String?): MviBaseView<String, String> =
        object : MviBaseView<String, String>() {
            init {
                registerDiffByEquals(consumer, getValue, TestConsumer::setValue)
            }
        }

    private fun registerDiffByReference(getValue: String.() -> String?): MviBaseView<String, String> =
        object : MviBaseView<String, String>() {
            init {
                registerDiffByReference(consumer, getValue, TestConsumer::setValue)
            }
        }

    private fun createString(value: String): String = StringBuilder(value).toString()

    private interface TestConsumer {
        fun setValue(value: String?)
    }

    private class TestView : MviBaseView<String, String>() {


        fun dispatchEvent() {
            dispatch("event")
        }
    }
}
