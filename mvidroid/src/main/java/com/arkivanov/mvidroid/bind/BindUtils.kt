package com.arkivanov.mvidroid.bind

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.MainThread
import com.arkivanov.mvidroid.component.MviComponent
import com.arkivanov.mvidroid.view.MviView
import io.reactivex.disposables.Disposable

/**
 * Binds Component with Views. Must be called only from Main thread.
 *
 * @param component a Component to bind to View, see [MviComponent]
 * @param bundles array of View Bundles to bind to Component, see [MviViewBundle]
 * @param UiEvent type of View's UI Events
 * @param States type of Component's States
 * @param Component type of Component
 * @return [MviLifecycleObserver], use it to control life-cycle
 */
@MainThread
fun <UiEvent : Any, States : Any, Component : MviComponent<UiEvent, States>> bind(
    component: Component,
    vararg bundles: MviViewBundle<States, *, out UiEvent>
): MviLifecycleObserver {
    val disposables = ArrayList<Disposable>(bundles.size * 2)

    bundles.forEach {
        disposables.add(it.view.uiEvents.subscribe { component(it) })
    }

    return object : MviLifecycleObserver {
        override fun onStart() {
            bundles.forEach { disposables.add(it.subscribe(component.states)) }
        }

        override fun onStop() {
            disposables.forEach(Disposable::dispose)
            disposables.clear()
        }

        override fun onDestroy() {
            component.dispose()
        }
    }
}

/**
 * Binds Component to Views using Android Arch's LifecycleOwner. Can be called directly from activity or fragment.
 * Must be called only on Main thread. Uses DefaultLifecycleObserver which requires Java 8 source compatibility.
 * To setup Java 8 source compatibility just add the following lines into your app's build.gradle file:
 *
 * ```
 * android {
 *     ...
 *
 *     compileOptions {
 *         sourceCompatibility JavaVersion.VERSION_1_8
 *         targetCompatibility JavaVersion.VERSION_1_8
 *     }
 *     ...
 * }
 * ```
 *
 * See [bind] for mor information.
 */
@MainThread
fun <UiEvent : Any, States : Any, Component : MviComponent<UiEvent, States>> LifecycleOwner.bind(
    component: Component,
    vararg bundles: MviViewBundle<States, *, out UiEvent>
) {
    com.arkivanov.mvidroid.bind.bind(component, *bundles).also { observer ->
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                observer.onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                observer.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                observer.onDestroy()
            }
        })
    }
}

/**
 * Helper infix method that creates [MviViewBundle] from [MviView] and [MviViewModelMapper]
 * @param mapper a View Model Mapper that should be used to transform States to View Models
 * @param ViewModel type of View Model
 * @param UiEvent type of View's UI Events
 * @param States type of Component's States
 */
infix fun <ViewModel : Any, UiEvent : Any, States : Any> MviView<ViewModel, UiEvent>.using(
    mapper: MviViewModelMapper<States, ViewModel>
): MviViewBundle<States, ViewModel, UiEvent> = MviViewBundle(this, mapper)

@MainThread
private fun <States : Any, ViewModel : Any> MviViewBundle<States, ViewModel, *>.subscribe(states: States): Disposable =
    view.subscribe(mapper.map(states))
