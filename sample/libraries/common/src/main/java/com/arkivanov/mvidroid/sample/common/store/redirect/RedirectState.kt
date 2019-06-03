package com.arkivanov.mvidroid.sample.common.store.redirect

import com.arkivanov.mvidroid.sample.common.utils.SingleLifeEvent
import java.io.Serializable

data class RedirectState<out T : Any>(
    val redirect: SingleLifeEvent<T> = SingleLifeEvent()
) : Serializable