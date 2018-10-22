package com.arkivanov.mvidroid.sample.common.store.redirect

data class RedirectState<out T : Any>(
    val redirect: T? = null
)