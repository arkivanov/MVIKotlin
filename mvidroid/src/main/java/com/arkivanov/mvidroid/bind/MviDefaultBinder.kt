package com.arkivanov.mvidroid.bind

import android.support.annotation.CheckResult
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.utils.mapNotNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

internal class MviDefaultBinder<in ComponentEvent : Any, out ComponentStates : Any>(
    private val component: MviComponent<ComponentEvent, ComponentStates>
) : MviBinder<ComponentEvent, ComponentStates> {

    private var disposeComponent: Boolean = true
    private val views = ArrayList<MviViewBundle<ComponentStates, ComponentEvent>>()

    override fun setDisposeComponent(disposeComponent: Boolean): MviBinder<ComponentEvent, ComponentStates> =
        also { this.disposeComponent = disposeComponent }

    override fun addViewBundle(bundle: MviViewBundle<ComponentStates, ComponentEvent>): MviBinder<ComponentEvent, ComponentStates> =
        also { views.add(bundle) }

    @MainThread
    @CheckResult
    override fun bind(): MviLifecycleObserver = bind(component, disposeComponent, ArrayList(views))

    private companion object {
        @CheckResult
        private fun <ComponentStates : Any, ComponentEvent : Any> bind(
            component: MviComponent<ComponentEvent, ComponentStates>,
            disposeComponent: Boolean,
            views: List<MviViewBundle<ComponentStates, ComponentEvent>>
        ): MviLifecycleObserver {
            val onStopDisposables = ArrayList<Disposable>(views.size)
            val onDestroyDisposables = CompositeDisposable()

            views.forEach { bundle ->
                bundle
                    .view
                    .events
                    .mapNotNull(bundle.eventMapper)
                    .subscribe { component.accept(it) }
                    .also { onDestroyDisposables.add(it) }
            }

            return object : MviLifecycleObserver {
                override fun onStart() {
                    views.forEach { bundle ->
                        bundle
                            .modelMapper(component.states)
                            .subscribe { bundle.view.bind(it) }
                            .also { onStopDisposables.add(it) }
                    }
                }

                override fun onStop() {
                    onStopDisposables.forEach(Disposable::dispose)
                    onStopDisposables.clear()
                }

                override fun onDestroy() {
                    onDestroyDisposables.dispose()
                    views.forEach { it.view.onDestroy() }
                    if (disposeComponent) {
                        component.dispose()
                    }
                }
            }
        }
    }
}
