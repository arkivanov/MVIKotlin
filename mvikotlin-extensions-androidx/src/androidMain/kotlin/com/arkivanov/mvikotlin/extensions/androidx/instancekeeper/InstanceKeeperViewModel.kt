package com.arkivanov.mvikotlin.extensions.androidx.instancekeeper

import androidx.lifecycle.ViewModel
import com.arkivanov.mvikotlin.core.instancekeeper.ExperimentalInstanceKeeperApi
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceContainer
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeperProvider
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.destroy
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle
import androidx.lifecycle.Lifecycle as AndroidLifecycle

@ExperimentalInstanceKeeperApi
internal class InstanceKeeperViewModel : ViewModel() {

    private val lifecycleRegistry = LifecycleRegistry()
    val instanceKeeperProvider: InstanceKeeperProvider = InstanceContainer(lifecycleRegistry)
    private var isCreated = false
    private var isAttached = false

    fun attachLifecycleIfNeeded(lifecycle: AndroidLifecycle) {
        if (isAttached) {
            return
        }

        isAttached = true

        lifecycle.asMviLifecycle().subscribe(
            object : Lifecycle.Callbacks by lifecycleRegistry {
                override fun onCreate() {
                    if (!isCreated) {
                        isCreated = true
                        lifecycleRegistry.onCreate()
                    }
                }

                override fun onDestroy() {
                    isAttached = false
                }
            }
        )
    }

    override fun onCleared() {
        lifecycleRegistry.destroy()

        super.onCleared()
    }

}
