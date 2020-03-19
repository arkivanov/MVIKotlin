package com.arkivanov.mvikotlin.core.utils.statekeeper

import android.os.Bundle
import android.os.Parcelable

@Suppress("FunctionName") // Factory function
fun ParcelableStateKeeperContainer(): ParcelableStateKeeperContainer =
    object : ParcelableStateKeeperContainer, StateKeeperContainer<Bundle, Parcelable> by StateKeeperContainer(
        get = { bundle, key -> bundle.getParcelable(key) },
        put = Bundle::putParcelable
    ) {
    }
