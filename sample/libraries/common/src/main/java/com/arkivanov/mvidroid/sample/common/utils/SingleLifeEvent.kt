package com.arkivanov.mvidroid.sample.common.utils

import java.io.Serializable

class SingleLifeEvent<out T : Any>(event: T? = null) : Serializable {

    @Transient
    private var _event: T? = event

    val event: T? get() = _event

    fun notifyUsed() {
        _event = null
    }

    inline fun use(block: (T) -> Unit) {
        event?.also {
            notifyUsed()
            block(it)
        }
    }
}
