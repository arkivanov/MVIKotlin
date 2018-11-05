package com.arkivanov.mvidroid.bind

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.arkivanov.mvidroid.view.MviView
import io.reactivex.Observable

/**
 * Subscribes to Android Arch's Lifecycle events and calls appropriate methods of MviLifecycleObserver.
 * Uses DefaultLifecycleObserver which requires Java 8 source compatibility.
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
 * See [MviLifecycleObserver], [Lifecycle] and [LifecycleOwner] for more information.
 *
 * @param lifecycle an instance of Lifecycle to subscribe
 */
fun MviLifecycleObserver.attachTo(lifecycle: Lifecycle) {
    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                this@attachTo.onStart()
            }

            override fun onStop(owner: LifecycleOwner) {
                this@attachTo.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                this@attachTo.onDestroy()
            }
        }
    )
}

/**
 * Convenience method, see MviLifecycleObserver.attachTo(Lifecycle) method for more information
 */
fun MviLifecycleObserver.attachTo(lifecycleOwner: LifecycleOwner) {
    attachTo(lifecycleOwner.lifecycle)
}

/** Adds multiple View Bundles, see [MviViewBundle] for more information
 *
 * @param bundles an Iterable containing all View Bundles
 * @return same instance of MviBinder for method chaining
 */
fun <ComponentStates : Any, ComponentEvent : Any> MviBinder<ComponentStates, ComponentEvent>.addViewBundles(
    bundles: Iterable<MviViewBundle<ComponentStates, ComponentEvent>>
): MviBinder<ComponentStates, ComponentEvent> {
    bundles.forEach { addViewBundle(it) }
    return this
}

/**
 * Adds View and its mappers to MviBinder, see [MviBinder] for more information
 *
 * @param view an instance of [MviView]
 * @param modelMapper a View Model Mapper responsible for mappings Component States to View Models
 * @param eventMapper a View Event Mapper responsible for mappings View Events to Component Events
 * @return same instance of MviBinder for method chaining
 */
fun <ComponentStates : Any, ComponentEvent : Any, ViewModel : Any, ViewEvent : Any> MviBinder<ComponentStates, ComponentEvent>.addView(
    view: MviView<ViewModel, ViewEvent>,
    modelMapper: (ComponentStates) -> Observable<out ViewModel>,
    eventMapper: (ViewEvent) -> ComponentEvent?
): MviBinder<ComponentStates, ComponentEvent> = addViewBundle(MviViewBundle.create(view, modelMapper, eventMapper))

/**
 * Adds View and its View Model Mapper to MviBinder.
 * For views whose Event types are same as Component Event type. See [MviBinder] for more information.
 *
 * @param view an instance of [MviView]
 * @param modelMapper a View Model Mapper responsible for mappings Component States to View Models
 * @return same instance of MviBinder for method chaining
 */
fun <ComponentStates : Any, ComponentEvent : Any, ViewModel : Any> MviBinder<ComponentStates, ComponentEvent>.addView(
    view: MviView<ViewModel, ComponentEvent>,
    modelMapper: (ComponentStates) -> Observable<out ViewModel>
): MviBinder<ComponentStates, ComponentEvent> = addViewBundle(MviViewBundle.create(view, modelMapper))
