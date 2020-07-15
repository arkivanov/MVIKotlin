package com.arkivanov.mvikotlin.core.statekeeper

import com.badoo.reaktive.utils.ensureNeverFrozen
import kotlin.reflect.KClass

internal class StateKeeperControllerImpl<in C : Any, in T : Any>(
    private val savedState: () -> C?,
    private val get: C.(key: String, clazz: KClass<out T>) -> T?,
    private val put: C.(key: String, clazz: KClass<out T>, value: T) -> Unit
) : StateKeeperController<C, T> {

    init {
        ensureNeverFrozen()
    }

    private val suppliers = HashMap<String, Pair<KClass<out T>, () -> T>>()

    override fun <S : T> get(clazz: KClass<out S>, key: String): StateKeeper<S> =
        object : StateKeeper<S> {
            @Suppress("UNCHECKED_CAST")
            override fun getState(): S? = savedState()?.get(key, clazz) as S?

            override fun register(supplier: () -> S) {
                check(key !in suppliers) { "The supplier is already register with this key: $key" }
                suppliers[key] = clazz to supplier
            }
        }

    override fun save(container: C) {
        suppliers.forEach { (key, classAndSupplier) ->
            container.put(key, classAndSupplier.first, classAndSupplier.second())
        }
    }
}
