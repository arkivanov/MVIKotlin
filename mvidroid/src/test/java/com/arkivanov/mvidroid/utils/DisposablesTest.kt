package com.arkivanov.mvidroid.utils

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class DisposablesTest {

    private val impl = Disposables()

    @Test
    fun `WHEN disposed THEN isDisposed=true`() {
        impl.dispose()

        assertTrue(impl.isDisposed)
    }

    @Test
    fun `WHEN disposable added THEN contains`() {
        val disposable: Disposable = mock()

        impl.add(disposable)

        assertTrue(impl.contains(disposable))
    }

    @Test
    fun `WHEN disposable added AND disposed THEN disposable disposed`() {
        val disposable: Disposable = mock()
        impl.add(disposable)

        impl.dispose()

        verify(disposable).dispose()
    }

    @Test
    fun `WHEN disposable added AND disposed THEN not contains`() {
        val disposable: Disposable = mock()
        impl.add(disposable)

        impl.dispose()

        assertFalse(impl.contains(disposable))
    }

    @Test
    fun `WHEN disposable added AND disposable disposed AND another disposable added THEN not contains first disposable`() {
        val disposable1: Disposable = mock {
            on { isDisposed }.thenReturn(true)
        }
        val disposable2: Disposable = mock()

        impl.add(disposable1)
        impl.add(disposable2)

        assertFalse(impl.contains(disposable1))
    }
}
