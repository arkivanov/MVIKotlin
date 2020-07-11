package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.arkivanov.mvikotlin.utils.internal.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.utils.internal.plusAssign
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.freeze
import com.badoo.reaktive.utils.isFrozen
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ThreadLocalSubjectTest {

    private val subject = ThreadLocalSubject<Int?>().freeze()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
    }

    @AfterTest
    fun after() {
        isAssertOnMainThreadEnabled = false
    }

    @Test
    fun multicasts_values_to_all_subscribers() {
        val values1 = AtomicList<Int?>()
        val values2 = AtomicList<Int?>()

        subject.subscribe(observer(onNext = values1::add))
        subject.onNext(0)
        subject.onNext(null)
        subject.subscribe(observer(onNext = values2::add))
        subject.onNext(1)
        subject.onNext(null)

        assertEquals(listOf(0, null, 1, null), values1.value)
        assertEquals(listOf(1, null), values2.value)
    }

    @Test
    fun completes_all_existing_observers_WHEN_completed() {
        val isCompleted1 = AtomicBoolean()
        val isCompleted2 = AtomicBoolean()

        subject.subscribe(observer = observer(onComplete = {
            isCompleted1.value = true
        }))
        subject.onNext(0)
        subject.subscribe(observer = observer(onComplete = {
            isCompleted2.value = true
        }))
        subject.onNext(1)
        subject.onComplete()

        assertTrue(isCompleted1.value)
        assertTrue(isCompleted2.value)
    }

    @Test
    fun completes_new_observer_WHEN_already_completed_and_new_observer_subscribed() {
        val isCompleted1 = AtomicBoolean()

        subject.onComplete()
        subject.subscribe(observer = observer(onComplete = {
            isCompleted1.value = true
        }))

        assertTrue(isCompleted1.value)
    }

    @Test
    fun does_not_produce_values_to_unsubscribed_observers() {
        val hasValue = AtomicBoolean()

        subject.subscribe(observer { hasValue.value = true }).dispose()
        subject.onNext(0)

        assertFalse(hasValue.value)
    }

    @Test
    fun produces_values_to_another_observers_WHEN_one_observer_unsubscribed_and_new_values() {
        val disposable1 = subject.subscribe(observer())
        val values2 = AtomicList<Int?>()

        subject.subscribe(observer(onNext = values2::add))
        disposable1.dispose()
        subject.onNext(0)
        subject.onNext(null)

        assertEquals(listOf(0, null), values2.value)
    }

    @Test
    fun disposables_are_disposed_WHEN_completed() {
        val disposable1 = subject.subscribe(observer())
        val disposable2 = subject.subscribe(observer())

        subject.onComplete()

        assertTrue(disposable1.isDisposed)
        assertTrue(disposable2.isDisposed)
    }

    @Test
    fun disposable_is_disposed_WHEN_already_completed_and_new_observer_subscribed() {
        subject.onComplete()
        val disposable = subject.subscribe(observer())

        assertTrue(disposable.isDisposed)
    }

    @Test
    fun subscriber_not_frozen() {
        val observer = observer<Int?>()

        subject.subscribe(observer)

        assertFalse(observer.isFrozen)
    }

    @Test
    fun does_not_emit_values_recursively() {
        val isEmitting = AtomicBoolean()
        val isEmittedRecursively = AtomicBoolean()

        subject.subscribe(
            observer { value ->
                if (value == 1) {
                    isEmitting.value = true
                    subject.onNext(2)
                    isEmitting.value = false
                } else {
                    isEmittedRecursively.value = isEmitting.value
                }
            }
        )

        subject.onNext(1)

        assertFalse(isEmittedRecursively.value)
    }

    @Test
    fun emits_all_values_in_order_WHEN_onNext_called_recursively() {
        val values = AtomicList<Int?>()

        subject.subscribe(
            observer { value ->
                if (value == 1) {
                    subject.onNext(null)
                    subject.onNext(2)
                }
                values += value
            }
        )

        subject.onNext(1)

        assertEquals(values.value, listOf(1, null, 2))
    }

    @Test
    fun does_no_complete_recursively() {
        val isEmitting = AtomicBoolean()
        val isCompletedRecursively = AtomicBoolean()

        subject.subscribe(
            observer(
                onComplete = { isCompletedRecursively.value = isEmitting.value },
                onNext = {
                    isEmitting.value = true
                    subject.onComplete()
                    isEmitting.value = false
                }
            )
        )

        subject.onNext(1)

        assertFalse(isCompletedRecursively.value)
    }

    @Test
    fun completes_WHEN_onComplete_called_recursively() {
        val isCompleted = AtomicBoolean()

        subject.subscribe(
            observer(
                onComplete = { isCompleted.value = true },
                onNext = { subject.onComplete() }
            )
        )

        subject.onNext(1)

        assertTrue(isCompleted.value)
    }
}
