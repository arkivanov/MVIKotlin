package com.arkivanov.mvikotlin.core.store

class SimpleBootstrapper<Action>(
    private vararg val actions: Action
) : BaseBootstrapper<Action>() {

    override fun bootstrap() {
        actions.forEach(::dispatch)
    }
}
