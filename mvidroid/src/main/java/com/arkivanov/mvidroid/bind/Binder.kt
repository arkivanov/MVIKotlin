package com.arkivanov.mvidroid.bind

import android.support.annotation.CheckResult
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.view.MviView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Binds Component withs Views.
 * Responsibilities:
 * * Transforms Component's States to View Models
 * * Subscribes all Views to their View Models on life-cycle "onStart" event
 * * Unsubscribes all Views from their View Models on life-cycle "onStop" event
 * * Disposes Component (if needed) and all Views on life-cycle "onDestroy" event
 */
class Binder<UiEvent : Any, States : Any>(
    private val component: MviComponent<UiEvent, States>
) {

    private var disposeComponent: Boolean = true
    private val views = ArrayList<MviViewBundle<States, *, UiEvent>>()

    /**
     * Whether the Component should be disposed or not at the end of life-cycle
     *
     * @param disposeComponent if true then Component will disposed at the end of life-cycle,
     * default is true
     * @return same instance of Binder for method chaining
     */
    fun setDisposeComponent(disposeComponent: Boolean): Binder<UiEvent, States> =
        this.also { this.disposeComponent = disposeComponent }

    /**
     * Adds View and its View Model Mapper
     *
     * @param view an instance of [MviView]
     * @param mapper a View Model Mapper [MviViewModelMapper] responsible for mappings Component's States to View Models
     * @param ViewModel type of View Model
     * @return same instance of Binder for method chaining
     */
    fun <ViewModel : Any> addView(
        view: MviView<ViewModel, UiEvent>,
        mapper: MviViewModelMapper<States, ViewModel>
    ): Binder<UiEvent, States> = this.also { views.add(MviViewBundle(view, mapper)) }

    /**
     * Adds View Bundle, see [MviViewBundle] for more information
     *
     * @param bundle a bundle to be added
     * @param ViewModel type of View Model
     * @return same instance of Binder for method chaining
     */
    fun <ViewModel : Any> addView(bundle: MviViewBundle<States, ViewModel, UiEvent>): Binder<UiEvent, States> =
        this.also { views.add(bundle) }

    /**
     * Adds multiple View Bundles, see [MviViewBundle] for more information
     *
     * @param bundles an Iterable containing all View Bundles
     * @return same instance of Binder for method chaining
     */
    fun addViews(bundles: Iterable<MviViewBundle<States, *, UiEvent>>): Binder<UiEvent, States> =
        this.also { views.addAll(bundles) }

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
        private fun <UiEvent : Any, States : Any> bind(
            component: MviComponent<UiEvent, States>,
            disposeComponent: Boolean,
            views: List<MviViewBundle<States, *, out UiEvent>>
        ): MviLifecycleObserver {
            val onStopDisposables = ArrayList<Disposable>(views.size)
            val onDestroyDisposables = CompositeDisposable()

            views.forEach { bundle ->
                onDestroyDisposables.add(bundle.view.uiEvents.subscribe { component(it) })
            }

            return object : MviLifecycleObserver {
                override fun onStart() {
                    views.forEach { onStopDisposables.add(it.subscribe(component.states)) }
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

        private fun <States : Any, ViewModel : Any> MviViewBundle<States, ViewModel, *>.subscribe(states: States): Disposable =
            mapper
                .map(states)
                .subscribe { view.bind(it) }
    }
}