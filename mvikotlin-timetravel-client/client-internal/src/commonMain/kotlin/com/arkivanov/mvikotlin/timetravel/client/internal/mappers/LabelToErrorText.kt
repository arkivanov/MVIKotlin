package com.arkivanov.mvikotlin.timetravel.client.internal.mappers

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientStore

internal val labelToErrorText: TimeTravelClientStore.Label.() -> String? =
    {
        when (this) {
            is TimeTravelClientStore.Label.Error -> text ?: "Unknown error"
        }
    }
