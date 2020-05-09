package com.arkivanov.mvikotlin.core.statekeeper

import android.os.Bundle
import java.io.Serializable

@Suppress("FunctionName") // Factory function
fun SerializableStateKeeperContainer(): SerializableStateKeeperContainer =
    object : SerializableStateKeeperContainer, StateKeeperContainer<Bundle, Serializable> by StateKeeperContainer(
        get = Bundle::getSerializable,
        put = Bundle::putSerializable
    ) {
    }
