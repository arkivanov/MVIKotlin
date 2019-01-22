package com.arkivanov.mvidroid.utils

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class ModelDiffTest {

    private val mapper = Model::value
    private val consumer = mock<(String?) -> Unit>()
    private val diff = ModelDiff<Model>()

    @Before
    fun before() {
        diff.diff(mapper, { a, b -> a == b }, consumer)
    }

    @Test
    fun `consumer called with first null WHEN first value is null`() {
        accept(null)
        verify(consumer).invoke(null)
    }

    @Test
    fun `consumer called with first value WHEN first value is not null`() {
        accept("value")
        verify(consumer).invoke("value")
    }

    @Test
    fun `consumer called with second value WHEN first value is null and second value is not null`() {
        accept(null)
        accept("value")
        verify(consumer).invoke("value")
    }

    @Test
    fun `consumer called with second value WHEN first and second values are different`() {
        accept("value1")
        accept("value2")
        verify(consumer).invoke("value2")
    }

    @Test
    fun `consumer called once WHEN first and second values are equal`() {
        accept("value")
        accept("value")
        verify(consumer).invoke("value")
    }

    @Test
    fun `consumer called with third value WHEN first and second values are equal and third value is different`() {
        accept("value1")
        accept("value1")
        accept("value2")
        verify(consumer).invoke("value2")
    }

    private fun accept(value: String?) {
        diff.accept(Model(value))
    }

    class Model(val value: String?)
}
