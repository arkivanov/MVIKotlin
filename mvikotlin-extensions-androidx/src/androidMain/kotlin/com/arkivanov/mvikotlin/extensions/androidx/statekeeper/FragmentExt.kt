package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import androidx.fragment.app.Fragment
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider

/**
 * Creates a new instance of [StateKeeperProvider] and attaches it to the [Fragment].
 * All registered state suppliers will be automatically called and all collected state
 * will be retained over configuration change.
 * The [Fragment] must be attached when calling this method.
 */
fun Fragment.retainingStateKeeperProvider(): StateKeeperProvider<Any> =
    retainingStateKeeperProvider { requireActivity().isChangingConfigurations }
