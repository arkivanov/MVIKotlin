package com.arkivanov.mvikotlin.core.store

import com.arkivanov.mvikotlin.core.annotations.MainThread

interface Bootstrapper<out Action> {

    @MainThread
    fun init(actionConsumer: (Action) -> Unit)

    @MainThread
    operator fun invoke()

    @MainThread
    fun dispose()
}
