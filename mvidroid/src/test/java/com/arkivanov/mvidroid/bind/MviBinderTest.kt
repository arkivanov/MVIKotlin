package com.arkivanov.mvidroid.bind

import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.view.MviView
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MviBinderTest {

    private val component = mock<MviComponent<String, String>> {
        on { states }.thenReturn("states")
    }

    private val uiEventSubject = PublishSubject.create<String>()
    private val viewModels = ArrayList<String>()
    private val view = mock<MviView<String, String>> { _ ->
        on { events }.thenReturn(uiEventSubject)
        on { bind(any()) }.thenAnswer {
            viewModels.add(it.getArgument(0))
            null
        }
    }

    private val viewModelSubject = PublishSubject.create<String>()
    private val viewModelMapper = mock<(String) -> Observable<out String>> {
        on { invoke("states") }.thenReturn(viewModelSubject)
    }

    private val viewEventMapper = mock<(String) -> String> {
        on { invoke("view_event") }.thenReturn("component_event")
    }

    private val binder = binder(component).addViewBundle(viewBundle(view, viewModelMapper, viewEventMapper))
    private var observer = binder.bind()

    @Test
    fun `component received event WHEN view published`() {
        uiEventSubject.onNext("view_event")
        verify(component).accept("component_event")
    }

    @Test
    fun `component received event WHEN stopped AND view published`() {
        observer.onStart()
        observer.onStop()
        uiEventSubject.onNext("view_event")
        verify(component).accept("component_event")
    }

    @Test
    fun `view model not received by view WHEN emitted AND observer not started`() {
        viewModelSubject.onNext("model")
        assertTrue(viewModels.isEmpty())
    }

    @Test
    fun `view model received by view WHEN emitted AND observer started`() {
        observer.onStart()
        viewModelSubject.onNext("model")
        assertEquals(listOf("model"), viewModels)
    }

    @Test
    fun `2nd and 4th view models received by view WHEN 1-start-2-stop-3-start-4-stop-5`() {
        viewModelSubject.onNext("1")
        observer.onStart()
        viewModelSubject.onNext("2")
        observer.onStop()
        viewModelSubject.onNext("3")
        observer.onStart()
        viewModelSubject.onNext("4")
        observer.onStop()
        viewModelSubject.onNext("5")
        assertEquals(listOf("2", "4"), viewModels)
    }

    @Test
    fun `component disposed WHEN observer destroyed`() {
        observer.onDestroy()
        verify(component).dispose()
    }

    @Test
    fun `view disposed WHEN observer destroyed`() {
        observer.onDestroy()
        verify(view).onDestroy()
    }

    @Test
    fun `component not disposed WHEN observer destroyed and disposeComponent flag if false`() {
        binder.setDisposeComponent(false)
        observer = binder.bind()
        observer.onDestroy()
        verify(component, never()).dispose()
    }
}
