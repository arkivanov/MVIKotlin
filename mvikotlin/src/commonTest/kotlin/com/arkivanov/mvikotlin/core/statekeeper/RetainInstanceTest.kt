package com.arkivanov.mvikotlin.core.statekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.TestLifecycle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class RetainInstanceTest {

    private var stateKeeperProvider = TestStateKeeperProvider()
    private var lifecycle = TestLifecycle()

    @Test
    fun creates_new_instance_WHEN_no_retained_state() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)

        assertNotNull(instance)
    }

    @Test
    fun wrapped_lifecycle_initialized_WHEN_original_lifecycle_initialized() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)

        assertEquals(Lifecycle.State.INITIALIZED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_created_WHEN_original_lifecycle_created() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()

        assertEquals(Lifecycle.State.CREATED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_started_WHEN_original_lifecycle_started() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onStart()

        assertEquals(Lifecycle.State.STARTED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_resumed_WHEN_original_lifecycle_resumed() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()

        assertEquals(Lifecycle.State.RESUMED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_started_WHEN_original_lifecycle_paused() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()
        lifecycle.onPause()

        assertEquals(Lifecycle.State.STARTED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_created_WHEN_original_lifecycle_stopped() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onStop()

        assertEquals(Lifecycle.State.CREATED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_destroyed_WHEN_instance_not_retained_and_original_lifecycle_destroyed() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onDestroy()

        assertEquals(Lifecycle.State.DESTROYED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_created_WHEN_instance_retained_and_original_lifecycle_destroyed() {
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        stateKeeperProvider.save()
        lifecycle.onDestroy()

        assertEquals(Lifecycle.State.CREATED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_created_WHEN_instance_retained_and_old_original_lifecycle_destroyed_and_new_original_lifecycle_initialized() {
        stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        val savedState = stateKeeperProvider.save()
        lifecycle.onDestroy()
        stateKeeperProvider = TestStateKeeperProvider(savedState)
        lifecycle = TestLifecycle()
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)

        assertEquals(Lifecycle.State.CREATED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_created_WHEN_instance_retained_and_old_original_lifecycle_destroyed_and_new_original_lifecycle_created() {
        stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        val savedState = stateKeeperProvider.save()
        lifecycle.onDestroy()
        stateKeeperProvider = TestStateKeeperProvider(savedState)
        lifecycle = TestLifecycle()
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()

        assertEquals(Lifecycle.State.CREATED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_started_WHEN_instance_retained_and_old_original_lifecycle_destroyed_and_new_original_lifecycle_started() {
        stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        val savedState = stateKeeperProvider.save()
        lifecycle.onDestroy()
        stateKeeperProvider = TestStateKeeperProvider(savedState)
        lifecycle = TestLifecycle()
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onStart()

        assertEquals(Lifecycle.State.STARTED, instance.lifecycle.state)
    }

    @Test
    fun wrapped_lifecycle_resumed_WHEN_instance_retained_and_old_original_lifecycle_destroyed_and_new_original_lifecycle_resumed() {
        stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        val savedState = stateKeeperProvider.save()
        lifecycle.onDestroy()
        stateKeeperProvider = TestStateKeeperProvider(savedState)
        lifecycle = TestLifecycle()
        val instance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        lifecycle.onStart()
        lifecycle.onResume()

        assertEquals(Lifecycle.State.RESUMED, instance.lifecycle.state)
    }

    @Test
    fun instance_retained_WHEN_state_saved_and_recreated() {
        val oldInstance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)
        lifecycle.onCreate()
        val savedState = stateKeeperProvider.save()
        lifecycle.onDestroy()
        stateKeeperProvider = TestStateKeeperProvider(savedState)
        lifecycle = TestLifecycle()
        val newInstance = stateKeeperProvider.retainInstance(lifecycle, "key", ::TestClass)

        assertSame(oldInstance, newInstance)
    }

    private class TestClass(
        val lifecycle: Lifecycle
    )

    private class TestStateKeeperProvider(
        private val savedState: Map<String, Any> = HashMap()
    ) : StateKeeperProvider<Any> {
        private val suppliers = HashMap<String, () -> Any>()

        fun save(): Map<String, Any> =
            suppliers.mapValues { it.value() }

        override fun <S : Any> get(key: String): StateKeeper<S> =
            object : StateKeeper<S> {
                @Suppress("UNCHECKED_CAST")
                override val state: S?
                    get() = savedState[key] as S?

                override fun register(supplier: () -> S) {
                    check(key !in suppliers)
                    suppliers += key to supplier
                }
            }
    }
}
