package com.arkivanov.mvidroid.utils

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscribeMviTest {

    private val source = PublishSubject.create<Unit>()
    private val consumer = mock<Consumer<Unit>>()
    private val observer = source.subscribeMvi(consumer)

    @Test
    fun `does not subscribe immediately`() {
        assertFalse(source.hasObservers())
    }

    @Test
    fun `subscribes WHEN onStart called`() {
        observer.onStart()

        assertTrue(source.hasObservers())
    }

    @Test
    fun `delivers values to consumer WHEN subscribed`() {
        observer.onStart()

        source.onNext(Unit)

        verify(consumer).accept(Unit)
    }

    @Test
    fun `unsubscribes WHEN onStop called`() {
        observer.onStart()

        observer.onStop()

        assertFalse(source.hasObservers())
    }

    @Test
    fun `resubscribes WHEN onStart called after onStop`() {
        observer.onStart()
        observer.onStop()

        observer.onStart()

        assertTrue(source.hasObservers())
    }
}