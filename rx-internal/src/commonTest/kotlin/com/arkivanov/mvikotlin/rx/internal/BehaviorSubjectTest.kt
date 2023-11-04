package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.observer
import kotlin.test.Test
import kotlin.test.assertEquals

class BehaviorSubjectTest {

    @Test
    fun produces_initial_value_WHEN_subscribed_with_value() {
        val subject = BehaviorSubject(0)
        val values = ArrayList<Int?>()

        subject.subscribe(observer(onNext = { values += it }))

        assertEquals(listOf<Int?>(0), values)
    }

    @Test
    fun does_not_produce_initial_value_to_new_observer_WHEN_already_completed_and_new_observer_subscribed_with_value() {
        val subject = BehaviorSubject(0)
        val values = ArrayList<Int?>()

        subject.onComplete()
        subject.subscribe(observer(onNext = { values += it }))

        assertEquals(emptyList(), values)
    }
}
