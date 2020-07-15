package com.arkivanov.mvikotlin.core.statekeeper

fun <C : Any> StateKeeperController<C, *>.saveAndGet(container: C): C {
    save(container)

    return container
}
