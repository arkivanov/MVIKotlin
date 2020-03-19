package com.arkivanov.mvikotlin.core.utils.statekeeper

@Suppress("FunctionName") // Factory function
fun SimpleStateKeeperContainer(): SimpleStateKeeperContainer =
    object : SimpleStateKeeperContainer, StateKeeperContainer<MutableMap<String, Any>, Any> by StateKeeperContainer(
        get = MutableMap<String, Any>::get,
        put = { mutableMap, key, value -> mutableMap[key] = value }
    ) {
    }
