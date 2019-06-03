package com.arkivanov.mvidroid.sample.common.store.redirect

import com.arkivanov.mvidroid.sample.common.store.redirect.RedirectStore.Intent
import com.arkivanov.mvidroid.store.MviStore
import java.io.Serializable

interface RedirectStore<T : Any> : MviStore<RedirectState<T>, Intent<T>, Nothing> {

    class Intent<out T : Any>(val redirect: T) : Serializable
}