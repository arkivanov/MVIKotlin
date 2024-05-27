package com.arkivanov.mvikotlin.core.instancekeeper

import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.mvikotlin.core.rx.Disposable
import com.arkivanov.mvikotlin.core.rx.Observer
import com.arkivanov.mvikotlin.core.store.Store
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

@Suppress("TestFunctionName")
class InstanceKeeperExtTest {

    private val keeper = InstanceKeeperDispatcher()

    @Test
    fun WHEN_getStore_with_same_type_THEN_same_instance() {
        val store1 = keeper.getStore { store<String, Float, Int>() }
        val store2 = keeper.getStore { store<String, Float, Int>() }

        assertSame(store1, store2)
    }

    @Test
    fun WHEN_getStore_with_different_types_THEN_instances_not_same() {
        val store1 = keeper.getStore { store<Int, String, Float>() }
        val store2 = keeper.getStore { store<String, Float, Int>() }

        assertNotSame<Store<*, *, *>>(store1, store2)
    }

    private fun <Intent : Any, State : Any, Label : Any> store(): Store<Intent, State, Label> =
        object : Store<Intent, State, Label> {
            override val state: State get() = error("Not implemented")
            override val isDisposed: Boolean get() = error("Not implemented")

            override fun states(observer: Observer<State>): Disposable =
                error("Not implemented")

            override fun labels(observer: Observer<Label>): Disposable =
                error("Not implemented")

            override fun accept(intent: Intent) {
                error("Not implemented")
            }

            override fun init() {
                error("Not implemented")
            }

            override fun dispose() {
                error("Not implemented")
            }
        }
}
