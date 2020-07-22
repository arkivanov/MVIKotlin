package com.arkivanov.mvikotlin.extensions.androidx.instancekeeper

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeperProvider

fun <T> T.getInstanceKeeperProvider(): InstanceKeeperProvider where T : ViewModelStoreOwner, T : LifecycleOwner {
    val viewModel = ViewModelProvider(this).get(InstanceKeeperViewModel::class.java)
    viewModel.attachLifecycleIfNeeded(lifecycle)

    return viewModel.instanceKeeperProvider
}
