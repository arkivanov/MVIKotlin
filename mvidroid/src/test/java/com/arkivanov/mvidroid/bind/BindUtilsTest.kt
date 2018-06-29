package com.arkivanov.mvidroid.bind

import com.arkivanov.mvidroid.bind.MviViewBundle
import com.arkivanov.mvidroid.bind.MviViewModelMapper
import com.arkivanov.mvidroid.bind.bind
import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.view.MviView
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BindUtilsTest {

    private val component = mock<MviComponent<String, String>> {
        on { states }.thenReturn("states")
    }

    private val uiEventRelay = PublishRelay.create<String>()
    private val viewModels = ArrayList<String>()
    private lateinit var viewModelsDisposable: Disposable
    private val view = mock<MviView<String, String>> {
        on { uiEvents }.thenReturn(uiEventRelay)
        on { subscribe(any()) }.thenAnswer {
            viewModelsDisposable = spy(it.getArgument<Observable<String>>(0).subscribe { viewModels.add(it) })
            viewModelsDisposable
        }
    }

    private val viewModelRelay = PublishRelay.create<String>()
    private val viewModelMapper = mock<MviViewModelMapper<String, String>> {
        on { map("states") }.thenReturn(viewModelRelay)
    }

    private val viewBundle = MviViewBundle(view, viewModelMapper)

    private val observer = bind(component, viewBundle)

    @Test
    fun `component received event WHEN view published`() {
        uiEventRelay.accept("event")
        verify(component)("event")
    }

    @Test
    fun `view model not received by view WHEN emitted AND observer not started`() {
        viewModelRelay.accept("model")
        assertTrue(viewModels.isEmpty())
    }

    @Test
    fun `view model received by view WHEN emitted AND observer started`() {
        observer.onStart()
        viewModelRelay.accept("model")
        assertEquals(listOf("model"), viewModels)
    }

    @Test
    fun `2nd and 4th view models received by view WHEN 1-start-2-stop-3-start-4-stop-5`() {
        viewModelRelay.accept("1")
        observer.onStart()
        viewModelRelay.accept("2")
        observer.onStop()
        viewModelRelay.accept("3")
        observer.onStart()
        viewModelRelay.accept("4")
        observer.onStop()
        viewModelRelay.accept("5")
        assertEquals(listOf("2", "4"), viewModels)
    }

    @Test
    fun `disposable from view not disposed WHEN observer started`() {
        observer.onStart()
        verify(viewModelsDisposable, never()).dispose()
    }

    @Test
    fun `disposable from view disposed WHEN observer stopped`() {
        observer.onStart()
        observer.onStop()
        verify(viewModelsDisposable).dispose()
    }

    @Test
    fun `component disposed WHEN observer destroyed`() {
        observer.onDestroy()
        verify(component).dispose()
    }
}
