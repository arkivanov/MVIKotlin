package com.arkivanov.mvikotlin.rx.internal

import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.utils.internal.atomic
import com.arkivanov.mvikotlin.utils.internal.freeze
import com.arkivanov.mvikotlin.utils.internal.getValue
import com.arkivanov.mvikotlin.utils.internal.setValue
import kotlin.test.Test
import kotlin.test.assertEquals

class BehaviorSubjectTest {

    @Test
    fun produces_initial_value_WHEN_subscribed_with_value() {
        val subject = BehaviorSubject(0).freeze()
        var values by atomic(emptyList<Int?>())

        subject.subscribe(observer(onNext = { values = values + it }))

        assertEquals(listOf(0), values)
    }

    @Test
    fun does_not_produce_initial_value_to_new_observer_WHEN_already_completed_and_new_observer_subscribed_with_value() {
        val subject = BehaviorSubject(0).freeze()
        var values by atomic(emptyList<Int?>())

        subject.onComplete()
        subject.subscribe(observer(onNext = { values = values + it }))

        assertEquals(emptyList(), values)
    }
}
