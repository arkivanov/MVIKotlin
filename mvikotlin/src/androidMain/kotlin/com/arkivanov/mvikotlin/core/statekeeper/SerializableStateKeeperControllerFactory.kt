package com.arkivanov.mvikotlin.core.statekeeper

import android.os.Bundle
import java.io.Serializable

@Suppress("FunctionName") // Factory function
fun SerializableStateKeeperController(savedState: () -> Bundle?): SerializableStateKeeperController =
    object : SerializableStateKeeperController, StateKeeperController<Bundle, Serializable> by StateKeeperControllerImpl(
        savedState = savedState,
        get = { key, clazz -> getSafe(key, clazz, Bundle::getSerializable) },
        put = { key, _, value -> putSafe(key, value, Bundle::putSerializable) }
    ) {
    }
