package com.arkivanov.mvikotlin.extensions.androidx.statekeeper

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import org.junit.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class RetainingStateKeeperProviderTest {

    @Test
    fun retains_state_WHEN_recreated_and_changing_configurations() {
        val viewModelStore = ViewModelStore()
        val oldOwner = Owner(viewModelStore)
        oldOwner.start()

        oldOwner.isChangingConfigurations = true
        oldOwner.destroy()
        val newOwner = Owner(viewModelStore)

        assertSame(oldOwner.data, newOwner.data)
    }

    @Test
    fun does_not_retain_state_WHEN_recreated_and_not_changing_configurations() {
        val viewModelStore = ViewModelStore()
        val oldOwner = Owner(viewModelStore)
        oldOwner.start()

        oldOwner.isChangingConfigurations = false
        oldOwner.destroy()
        val newOwner = Owner(viewModelStore)

        assertNotSame(oldOwner.data, newOwner.data)
    }

    private fun Owner.start() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    private fun Owner.destroy() {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    private class Data

    private class Owner(
        private val viewModelStore: ViewModelStore
    ) : ViewModelStoreOwner, LifecycleOwner {
        var isChangingConfigurations = false
        val lifecycle = LifecycleRegistry(this)
        private val provider = retainingStateKeeperProvider(::isChangingConfigurations)
        var stateKeeper = provider.get(clazz = Data::class, key = "key")
        val data = stateKeeper.getState() ?: Data()

        init {
            stateKeeper.register { data }
        }

        override fun getViewModelStore(): ViewModelStore = viewModelStore

        override fun getLifecycle(): Lifecycle = lifecycle
    }
}
