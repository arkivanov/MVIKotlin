package com.arkivanov.mvidroid.bind

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import com.arkivanov.mvidroid.view.MviView

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

/**
 * Helper infix method that creates [MviViewBundle] from [MviView] and [MviViewModelMapper]
 *
 * @param mapper a View Model Mapper that should be used to transform States to View Models
 * @param ViewModel type of View Model
 * @param UiEvent type of View's UI Events
 * @param States type of Component's States
 */
infix fun <ViewModel : Any, UiEvent : Any, States : Any> MviView<ViewModel, UiEvent>.using(
    mapper: MviViewModelMapper<States, ViewModel>
): MviViewBundle<States, ViewModel, UiEvent> = MviViewBundle(this, mapper)
