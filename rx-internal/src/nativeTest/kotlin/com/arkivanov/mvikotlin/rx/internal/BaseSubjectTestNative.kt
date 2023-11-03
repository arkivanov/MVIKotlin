package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.utils.internal.runOnBackgroundBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaseSubjectTestNative {

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun produces_values_WHEN_subscribed_on_background_thread_and_onNext_called_on_main_thread() {
        val values = ArrayList<Int?>()
        val subject = BaseSubject<Int?>()

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
        var isCompleted = false
        val subject = BaseSubject<Int?>()

        runOnBackgroundBlocking {
            subject.subscribe(observer(onComplete = { isCompleted = true }))
        }

        subject.onComplete()

        assertTrue(isCompleted)
    }
}
