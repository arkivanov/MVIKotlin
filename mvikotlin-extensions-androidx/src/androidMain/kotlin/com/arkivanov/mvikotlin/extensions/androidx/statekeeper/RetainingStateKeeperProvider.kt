package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.statekeeper.SimpleStateKeeperController
import com.arkivanov.mvikotlin.core.statekeeper.StateKeeperProvider
import com.arkivanov.mvikotlin.core.statekeeper.saveAndGet

internal inline fun <T> T.retainingStateKeeperProvider(
    crossinline isChangingConfigurations: () -> Boolean
): StateKeeperProvider<Any> where T : ViewModelStoreOwner, T : LifecycleOwner {
    val viewModel = ViewModelProvider(this).get(StateKeeperViewModel::class.java)
    val controller = SimpleStateKeeperController(viewModel::savedState)

    lifecycle.addObserver(
        object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                viewModel.savedState = null
            }

            override fun onStop(owner: LifecycleOwner) {
                if (isChangingConfigurations()) {
                    viewModel.savedState = controller.saveAndGet(HashMap())
                }
            }
        }
    )

    return controller
}
