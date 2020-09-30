package com.arkivanov.mvikotlin.utils.internal

import kotlin.test.Test
import kotlin.test.assertSame

class AtomicBooleanTest {

    @Test
    fun returns_initial_value_WHEN_created() {
        val ref = atomic(false)

        assertSame(false, ref.value)
    }

    @Test
    fun returns_new_value_WHEN_value_changed() {
        val ref = atomic(false)

        ref.value = true

        assertSame(true, ref.value)
    }
}
