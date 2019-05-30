package com.arkivanov.mvidroid.bind

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner

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
 * @param strategy see [ArchLifecycleAttachStrategy] for more details, default value is [ArchLifecycleAttachStrategy.START_STOP]
 */
fun <T : MviLifecycleObserver> T.attachTo(
    lifecycle: Lifecycle,
    strategy: ArchLifecycleAttachStrategy = ArchLifecycleAttachStrategy.START_STOP
): T {
    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                if (strategy === ArchLifecycleAttachStrategy.CREATE_DESTROY) {
                    onStart()
                }
            }

            override fun onStart(owner: LifecycleOwner) {
                if (strategy === ArchLifecycleAttachStrategy.START_STOP) {
                    onStart()
                }
            }

            override fun onResume(owner: LifecycleOwner) {
                if (strategy === ArchLifecycleAttachStrategy.RESUME_PAUSE) {
                    onStart()
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                if (strategy === ArchLifecycleAttachStrategy.RESUME_PAUSE) {
                    onStop()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                if (strategy === ArchLifecycleAttachStrategy.START_STOP) {
                    onStop()
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                if (strategy === ArchLifecycleAttachStrategy.CREATE_DESTROY) {
                    onStop()
                }

                onDestroy()
            }
        }
    )

    return this
}

/**
 * Convenience method, see [MviLifecycleObserver.attachTo] method for more information
 */
fun <T : MviLifecycleObserver> T.attachTo(
    lifecycleOwner: LifecycleOwner,
    strategy: ArchLifecycleAttachStrategy = ArchLifecycleAttachStrategy.START_STOP
): T = attachTo(lifecycleOwner.lifecycle, strategy)
