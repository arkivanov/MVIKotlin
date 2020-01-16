package com.arkivanov.mvikotlin.core.internal.rx

import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubjectTest {

    private val subject = Subject<Int?>().freeze()

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

        subject.subscribe(observer = observer(onComplete = { isCompleted1.value = true }))
        subject.onNext(0)
        subject.subscribe(observer = observer(onComplete = { isCompleted2.value = true }))
        subject.onNext(1)
        subject.onComplete()

        assertTrue(isCompleted1.value)
        assertTrue(isCompleted2.value)
    }

    @Test
    fun completes_new_observer_WHEN_already_completed_and_new_observer_subscribed() {
        val isCompleted1 = AtomicBoolean()

        subject.onComplete()
        subject.subscribe(observer = observer(onComplete = { isCompleted1.value = true }))

        assertTrue(isCompleted1.value)
    }

    @Test
    fun does_not_produce_values_to_unsubscribed_observers() {
        val hasValue = AtomicBoolean()

        subject.subscribe(observer(onNext = { hasValue.value = true })).dispose()
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
    fun produces_initial_value_WHEN_subscribed_with_value() {
        val values1 = AtomicList<Int?>()

        subject.subscribe(observer(onNext = values1::add), 0)

        assertEquals(listOf(0), values1.value)
    }

    @Test
    fun does_not_produce_initial_value_to_new_observer_WHEN_already_completed_and_new_observer_subscribed_with_value() {
        val values1 = AtomicList<Int?>()

        subject.onComplete()
        subject.subscribe(observer(onNext = values1::add), 0)

        assertEquals(emptyList(), values1.value)
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
}
