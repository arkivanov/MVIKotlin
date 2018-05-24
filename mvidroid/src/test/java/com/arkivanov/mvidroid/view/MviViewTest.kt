package com.arkivanov.mvidroid.view

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MviViewTest {

    private val models: Observable<String> = mock()
    private val disposable: Disposable = mock()
    private val binder: MviViewBinder<String> = mock {
        on { subscribe(any()) }.thenReturn(disposable)
    }
    private val impl = MviView(models, binder)

    @Test
    fun `WHEN subscribe THEN binder subscribed`() {
        impl.subscribe()

        verify(binder).subscribe(models)
    }

    @Test
    fun `WHEN subscribe twice THEN binder subscribed once`() {
        impl.subscribe()
        impl.subscribe()

        verify(binder).subscribe(models)
    }

    @Test
    fun `WHEN subscribe AND unsubscribe THEN disposable disposed`() {
        impl.subscribe()
        impl.unsubscribe()

        verify(disposable).dispose()
    }

    @Test
    fun `WHEN subscribe AND unsubscribe twice THEN disposable disposed once`() {
        impl.subscribe()
        impl.unsubscribe()
        impl.unsubscribe()

        verify(disposable).dispose()
    }
}
