package com.arkivanov.mvidroid.view

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Test

class MviAbstractViewTest {

    @Test
    fun `ui event emitted WHEN dispatched`() {
        val view = TestView()
        val observer = TestObserver<String>()
        view.uiEvents.subscribe(observer)
        view.dispatchEvent()
        assertEquals(1, observer.values().size)
        assertEquals("event", observer.values()[0])
    }

    private class TestView : MviAbstractView<String, String>() {
        override fun subscribe(models: Observable<String>): Disposable {
            throw NotImplementedError()
        }

        fun dispatchEvent() {
            dispatch("event")
        }
    }
}
