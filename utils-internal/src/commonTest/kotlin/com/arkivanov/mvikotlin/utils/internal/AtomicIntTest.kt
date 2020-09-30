package com.arkivanov.mvikotlin.utils.internal

import kotlin.test.Test
import kotlin.test.assertSame

class AtomicIntTest {

    @Test
    fun returns_initial_value_WHEN_created() {
        val ref = atomic(0)

        assertSame(0, ref.value)
    }

    @Test
    fun returns_new_value_WHEN_value_changed() {
        val ref = atomic(0)

        ref.value = 1

        assertSame(1, ref.value)
    }
}
