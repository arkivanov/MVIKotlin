package com.arkivanov.mvikotlin.core.statekeeper

import kotlin.js.JsName

@JsName("simpleStateKeeperController")
@Suppress("FunctionName") // Factory function
fun SimpleStateKeeperController(savedState: () -> MutableMap<String, Any>?): SimpleStateKeeperController =
    object : SimpleStateKeeperController, StateKeeperController<MutableMap<String, Any>, Any> by StateKeeperControllerImpl(
        savedState = savedState,
        get = { key, _ -> get(key) },
        put = { key, _, value -> put(key, value) }
    ) {
    }
