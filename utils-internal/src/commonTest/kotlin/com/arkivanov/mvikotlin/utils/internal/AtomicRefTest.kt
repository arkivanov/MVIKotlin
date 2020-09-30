package com.arkivanov.mvikotlin.utils.internal

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AtomicRefTest {

    @Test
    fun returns_initial_value_WHEN_created() {
        val value = Data()

        val ref = atomic(value)

        assertSame(value, ref.value)
    }

    @Test
    fun returns_new_value_WHEN_not_frozen_and_value_changed() {
        val ref = atomic(Data())
        val newValue = Data()

        ref.value = newValue

        assertSame(newValue, ref.value)
    }

    @Test
    fun returns_new_value_WHEN_frozen_and_value_changed() {
        val ref = atomic(Data())
        val newValue = Data()

        ref.freeze()
        ref.value = newValue

        assertSame(newValue, ref.value)

    }

    @Test
    fun new_value_not_frozen_WHEN_not_frozen_and_value_changed() {
        val ref = atomic(Data())
        val newValue = Data()

        ref.value = newValue

        assertFalse(newValue.isFrozen)
    }

    @Test
    fun compareAndSet_returns_false_WHEN_expected_value_not_same() {
        val ref = atomic(Data())

        val result = ref.compareAndSet(Data(), Data())

        assertFalse(result)
    }

    @Test
    fun returns_old_value_WHEN_compareAndSet_called_and_expected_value_not_same() {
        val oldValue = Data()
        val ref = atomic(oldValue)

        ref.compareAndSet(Data(), Data())

        assertSame(oldValue, ref.value)
    }

    @Test
    fun compareAndSet_returns_true_WHEN_expected_value_matches_is_same() {
        val oldValue = Data()
        val ref = atomic(oldValue)

        val result = ref.compareAndSet(oldValue, Data())

        assertTrue(result)
    }

    @Test
    fun returns_new_value_WHEN_compareAndSet_called_and_expected_value_is_same() {
        val oldValue = Data()
        val newValue = Data()
        val ref = atomic(oldValue)

        ref.compareAndSet(oldValue, newValue)

        assertSame(newValue, ref.value)
    }

    @Test
    fun getAndUpdate_returns_old_value() {
        val oldValue = Data()
        val ref = atomic(oldValue)

        val result = ref.getAndUpdate { Data() }

        assertSame(oldValue, result)
    }

    @Test
    fun returns_new_value_WHEN_getAndUpdate_called() {
        val newValue = Data()
        val ref = atomic(Data())

        ref.getAndUpdate { newValue }

        assertSame(newValue, ref.value)
    }

    private data class Data(
        val text: String = "text"
    )
}
