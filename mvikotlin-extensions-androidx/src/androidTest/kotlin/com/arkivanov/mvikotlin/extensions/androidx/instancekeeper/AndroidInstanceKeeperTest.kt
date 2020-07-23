package com.arkivanov.mvikotlin.extensions.androidx.instancekeeper

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.TestLifecycleCallbacks.Event
import org.junit.Test
import kotlin.test.assertSame
import androidx.lifecycle.Lifecycle as AndroidLifecycle

class AndroidInstanceKeeperTest {

    @Test
    fun retains_instance_WHEN_recreated() {
        val viewModelStore = ViewModelStore()
        val instance = Data()
        Owner(viewModelStore).keeper.instance = instance

        val newInstance = Owner(viewModelStore).keeper.instance

        assertSame(instance, newInstance)
    }

    @Test
    fun calls_lifecycle() {
        val callbacks = TestLifecycleCallbacks()

        val viewModelStore = ViewModelStore()
        val owner1 = Owner(viewModelStore)
        owner1.keeper.lifecycle.subscribe(callbacks)
        owner1.resume()
        owner1.destroy()
        val owner2 = Owner(viewModelStore)
        owner2.resume()
        owner2.destroy()
        viewModelStore.clear()

        callbacks.assertEvents(
            Event.ON_CREATE,
            Event.ON_START,
            Event.ON_RESUME,
            Event.ON_PAUSE,
            Event.ON_STOP,
            Event.ON_START,
            Event.ON_RESUME,
            Event.ON_PAUSE,
            Event.ON_STOP,
            Event.ON_DESTROY
        )
    }

    private fun Owner.resume() {
        lifecycle.handleLifecycleEvent(AndroidLifecycle.Event.ON_CREATE)
        lifecycle.handleLifecycleEvent(AndroidLifecycle.Event.ON_START)
        lifecycle.handleLifecycleEvent(AndroidLifecycle.Event.ON_RESUME)
    }

    private fun Owner.destroy() {
        lifecycle.handleLifecycleEvent(AndroidLifecycle.Event.ON_PAUSE)
        lifecycle.handleLifecycleEvent(AndroidLifecycle.Event.ON_STOP)
        lifecycle.handleLifecycleEvent(AndroidLifecycle.Event.ON_DESTROY)
    }

    private class Data

    private class Owner(
        private val viewModelStore: ViewModelStore
    ) : ViewModelStoreOwner, LifecycleOwner {
        val lifecycle = LifecycleRegistry(this)
        private val provider = getInstanceKeeperProvider()
        var keeper = provider.get<Data>(key = "key")

        override fun getViewModelStore(): ViewModelStore = viewModelStore

        override fun getLifecycle(): AndroidLifecycle = lifecycle
    }
}
