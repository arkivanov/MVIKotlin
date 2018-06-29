package com.arkivanov.mvidroid.utils

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisposablesTest {

    private val impl = Disposables()

    @Test
    fun `isDisposed=true WHEN disposed`() {
        impl.dispose()

        assertTrue(impl.isDisposed)
    }

    @Test
    fun `contains disposable WHEN added`() {
        val disposable: Disposable = mock()

        impl.add(disposable)

        assertTrue(impl.contains(disposable))
    }

    @Test
    fun `disposable disposed WHEN disposable added AND disposed`() {
        val disposable: Disposable = mock()
        impl.add(disposable)

        impl.dispose()

        verify(disposable).dispose()
    }

    @Test
    fun `not contains disposable WHEN disposable added AND disposed`() {
        val disposable: Disposable = mock()
        impl.add(disposable)

        impl.dispose()

        assertFalse(impl.contains(disposable))
    }

    @Test
    fun `not contains first disposable WHEN disposable added AND disposable disposed AND another disposable added`() {
        val disposable1: Disposable = mock {
            on { isDisposed }.thenReturn(true)
        }
        val disposable2: Disposable = mock()

        impl.add(disposable1)
        impl.add(disposable2)

        assertFalse(impl.contains(disposable1))
    }
}
