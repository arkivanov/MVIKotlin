package com.arkivanov.mvikotlin.timetravel.client.internal.settings.integration

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.SettingsConfig
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStore.State
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStoreFactory
import com.arkivanov.mvikotlin.timetravel.proto.internal.DEFAULT_PORT
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

internal class TimeTravelSettingsStoreSettings(
    settingsFactory: Settings.Factory,
    private val defaults: SettingsConfig.Defaults
) : TimeTravelSettingsStoreFactory.Settings {

    private val storage = settingsFactory.create(name = "TimeTravelSettings")

    override var settings: State.Settings
        get() = State.Settings(
            host = storage[KEY_HOST, DEFAULT_HOST],
            port = storage[KEY_PORT, DEFAULT_PORT],
            connectViaAdb = storage[KEY_CONNECT_VIA_ADB, defaults.connectViaAdb],
            wrapEventDetails = storage[KEY_WRAP_EVENT_DETAILS, false],
            isDarkMode = storage[KEY_DARK_MODE, false]
        )
        set(value) {
            storage[KEY_HOST] = value.host
            storage[KEY_PORT] = value.port
            storage[KEY_CONNECT_VIA_ADB] = value.connectViaAdb
            storage[KEY_DARK_MODE] = value.isDarkMode
            storage[KEY_WRAP_EVENT_DETAILS] = value.wrapEventDetails
        }

    private companion object {
        private const val KEY_HOST = "HOST"
        private const val KEY_PORT = "PORT"
        private const val KEY_CONNECT_VIA_ADB = "CONNECT_VIA_ADB"
        private const val KEY_WRAP_EVENT_DETAILS = "WRAP_EVENT_DETAILS"
        private const val KEY_DARK_MODE = "IS_DARK_MODE"
        private const val DEFAULT_HOST = "localhost"
    }
}
