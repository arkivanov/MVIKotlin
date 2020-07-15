package com.arkivanov.mvikotlin.core.statekeeper

import android.os.Bundle
import android.os.Parcelable

@Suppress("FunctionName") // Factory function
fun ParcelableStateKeeperController(savedState: () -> Bundle?): ParcelableStateKeeperController =
    object : ParcelableStateKeeperController, StateKeeperController<Bundle, Parcelable> by StateKeeperControllerImpl(
        savedState = savedState,
        get = { key, clazz -> getSafe(key, clazz) { getParcelable(it) } },
        put = { key, _, value -> putSafe(key, value, Bundle::putParcelable) }
    ) {
    }
