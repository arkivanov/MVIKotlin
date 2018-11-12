package com.arkivanov.mvidroid.bind

import android.support.annotation.CheckResult
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.component.MviAbstractComponent
import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.component.MviStoreBundle
import com.arkivanov.mvidroid.store.MviStore
import io.reactivex.Observable

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
interface MviBinder<in ComponentEvent : Any, out ComponentStates : Any> {

    /**
     * Whether the Component should be disposed or not at the end of life-cycle
     *
     * @param disposeComponent if true then Component will disposed at the end of life-cycle,
     * default is true
     * @return same instance of MviBinder for method chaining
     */
    fun setDisposeComponent(disposeComponent: Boolean): MviBinder<ComponentEvent, ComponentStates>

    /**
     * Adds View Bundle, see [MviViewBundle] for more information
     *
     * @param bundle a View Bundle to be added
     * @return same instance of MviBinder for method chaining
     */
    fun addViewBundle(bundle: MviViewBundle<ComponentStates, ComponentEvent>): MviBinder<ComponentEvent, ComponentStates>

    /**
     * Performs actual binding of Component and Views. Must be called only on Main thread.
     *
     * @return An observer of life-cycle events that should be used to control bindings over life-cycle.
     * See [MviLifecycleObserver] for more information.
     */
    @MainThread
    @CheckResult
    fun bind(): MviLifecycleObserver
}

fun <ComponentEvent : Any, ComponentStates : Any> binder(
    component: MviComponent<ComponentEvent, ComponentStates>
): MviBinder<ComponentEvent, ComponentStates> = MviDefaultBinder(component)

fun <Intent : Any, State : Any> binder(
    store: MviStore<State, Intent, *>
): MviBinder<Intent, Observable<out State>> =
    binder(
        object : MviAbstractComponent<Intent, Observable<out State>, Nothing>(
            stores = listOf(MviStoreBundle(store))
        ) {
            override val states: Observable<out State> = store.states
        }
    )
