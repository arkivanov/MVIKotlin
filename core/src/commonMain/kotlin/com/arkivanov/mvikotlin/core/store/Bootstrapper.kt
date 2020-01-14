package com.arkivanov.mvikotlin.core.store

interface Bootstrapper<out Action> {

    fun bootstrap(actionConsumer: (Action) -> Unit)

    fun dispose()
}
