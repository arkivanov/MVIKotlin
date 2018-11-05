package com.arkivanov.mvidroid.bind

import android.support.annotation.CheckResult
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.utils.mapNotNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Binds Component withs Views.
 * Responsibilities:
 * * Transforms Views' Events to Component Events using provided View Event Mappers
 * and subscribes Component to its Events
 * * Transforms Component's States to View Models using provided View Model Mappers
 * and subscribes all Views to their View Models on life-cycle "onStart" event
 * * Unsubscribes all Views from their View Models on life-cycle "onStop" event
 * * Disposes Component (if needed) and all Views on life-cycle "onDestroy" event
 */
class MviBinder<ComponentStates : Any, ComponentEvent : Any>(
    private val component: MviComponent<ComponentEvent, ComponentStates>
) {

    private var disposeComponent: Boolean = true
    private val views = ArrayList<MviViewBundle<ComponentStates, ComponentEvent>>()

    /**
     * Whether the Component should be disposed or not at the end of life-cycle
     *
     * @param disposeComponent if true then Component will disposed at the end of life-cycle,
     * default is true
     * @return same instance of MviBinder for method chaining
     */
    fun setDisposeComponent(disposeComponent: Boolean): MviBinder<ComponentStates, ComponentEvent> =
        this.also { this.disposeComponent = disposeComponent }

    /**
     * Adds View Bundle, see [MviViewBundle] for more information
     *
     * @param bundle a View Bundle to be added
     * @return same instance of MviBinder for method chaining
     */
    fun addViewBundle(bundle: MviViewBundle<ComponentStates, ComponentEvent>): MviBinder<ComponentStates, ComponentEvent> =
        also { views.add(bundle) }

    /**
     * Performs actual binding of Component and Views. Must be called only on Main thread.
     *
     * @return An observer of life-cycle events that should be used to control bindings over life-cycle.
     * See [MviLifecycleObserver] for more information.
     */
    @MainThread
    @CheckResult
    fun bind(): MviLifecycleObserver = bind(component, disposeComponent, ArrayList(views))

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