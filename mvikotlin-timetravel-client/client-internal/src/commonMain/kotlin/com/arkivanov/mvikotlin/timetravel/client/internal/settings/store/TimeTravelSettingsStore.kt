package com.arkivanov.mvikotlin.timetravel.client.internal.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStore.State

internal interface TimeTravelSettingsStore : Store<Intent, State, Nothing> {

    sealed class Intent {
        object StartEdit : Intent()
        object SaveEdit : Intent()
        object CancelEdit : Intent()
        data class SetHost(val host: String) : Intent()
        data class SetPort(val port: String) : Intent()
        data class SetConnectViaAdb(val connectViaAdb: Boolean) : Intent()
        data class SetWrapEventDetails(val wrapEventDetails: Boolean) : Intent()
        data class SetDarkMode(val isDarkMode: Boolean) : Intent()
    }

    data class State(
        val settings: Settings,
        val editing: Editing? = null
    ) {
        data class Settings(
            val host: String,
            val port: Int,
            val connectViaAdb: Boolean,
            val wrapEventDetails: Boolean,
            val isDarkMode: Boolean
        )

        data class Editing(
            val host: String,
            val port: String,
            val connectViaAdb: Boolean,
            val wrapEventDetails: Boolean,
            val isDarkMode: Boolean?
        )
    }
}
