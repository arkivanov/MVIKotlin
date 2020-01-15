package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface Bootstrapper<out Action> {

    @MainThread
    fun bootstrap(actionConsumer: (Action) -> Unit)

    @MainThread
    fun dispose()
}
