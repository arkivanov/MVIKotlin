package com.arkivanov.mvidroid.sample.common.store.redirect

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStore.Intent
import com.arkivanov.mvidroid.store.MviStore
import com.arkivanov.mvidroid.store.MviStoreFactory
import com.arkivanov.mvidroid.store.component.MviReducer
import com.arkivanov.mvidroid.store.createExecutorless
import kotlin.reflect.KClass

class RedirectStoreFactory(
    private val factory: MviStoreFactory
) {

    fun <T : Any> create(clazz: KClass<T>): RedirectStore<T> =
        object : MviStore<RedirectState<T>, Intent<T>, Nothing> by factory.createExecutorless(
            name = "RedirectStoreFactory<${clazz.java.simpleName}>",
            initialState = RedirectState<T>(),
            reducer = Reducer<T>()
        ), RedirectStore<T> {
        }

    inline fun <reified T : Any> create(): RedirectStore<T> = create(T::class)

    private class Reducer<T : Any> : MviReducer<RedirectState<T>, Intent<T>> {
        override fun RedirectState<T>.reduce(result: Intent<T>): RedirectState<T> = copy(redirect = result.redirect)
    }
}