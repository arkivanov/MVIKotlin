package com.arkivanov.mvidroid.view

import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class MviBaseViewTest {

    @Test
    fun `event emitted WHEN dispatched`() {
        val view = TestView()
        val observer = TestObserver<String>()
        view.events.subscribe(observer)
        view.dispatchTestEvent()
        assertEquals(1, observer.values().size)
        assertEquals("event", observer.values()[0])
    }

    private inner class TestView : MviBaseView<Nothing, String>() {
        fun dispatchTestEvent() {
            dispatch("event")
        }
    }
}
