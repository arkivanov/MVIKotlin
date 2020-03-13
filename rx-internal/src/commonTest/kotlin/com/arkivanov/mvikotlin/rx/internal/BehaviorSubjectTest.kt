package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.AtomicList
import com.arkivanov.mvikotlin.utils.internal.add
import com.badoo.reaktive.utils.freeze
import kotlin.test.Test
import kotlin.test.assertEquals

class BehaviorSubjectTest {

    @Test
    fun produces_initial_value_WHEN_subscribed_with_value() {
        val subject = BehaviorSubject(0).freeze()
        val values1 = AtomicList<Int?>()

        subject.subscribe(observer(onNext = { values1.add(it) }))

        assertEquals(listOf(0), values1.value)
    }

    @Test
    fun does_not_produce_initial_value_to_new_observer_WHEN_already_completed_and_new_observer_subscribed_with_value() {
        val subject = BehaviorSubject(0).freeze()
        val values1 = AtomicList<Int?>()

        subject.onComplete()
        subject.subscribe(observer(onNext = { values1.add(it) }))

        assertEquals(emptyList(), values1.value)
    }
}
