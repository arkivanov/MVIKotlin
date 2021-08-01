package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.utils.internal.isFrozen
import com.arkivanov.mvikotlin.utils.internal.setValue
import platform.posix.pthread_self
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThreadLocalSubjectTestNative {

    private val subject = ThreadLocalSubject<Int?>().freeze()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun subscribers_not_frozen() {
        val observer = observer<Int?>()

        subject.subscribe(observer)

        assertFalse(observer.isFrozen)
    }

    @Test
    fun produces_values_WHEN_subscribed_on_background_thread_and_onNext_called_on_main_thread() {
        var values by atomic<List<Int?>>(emptyList())
        val mainThreadId = pthread_self()
        val subject = ThreadLocalSubject<Int?>(isOnMainThread = { pthread_self() == mainThreadId })

        runOnBackgroundBlocking {
            subject.subscribe(observer(onNext = { values += it }))
        }

        subject.onNext(1)
        subject.onNext(null)
        subject.onNext(2)

        assertEquals(listOf(1, null, 2), values)
    }

    @Test
    fun completes_WHEN_subscribed_on_background_thread_and_onComplete_called_on_main_thread() {
        var isCompleted by atomic(false)
        val mainThreadId = pthread_self()
        val subject = ThreadLocalSubject<Int?>(isOnMainThread = { pthread_self() == mainThreadId })

        runOnBackgroundBlocking {
            subject.subscribe(observer(onComplete = { isCompleted = true }))
        }

        subject.onComplete()

        assertTrue(isCompleted)
    }
}
