package com.arkivanov.mvikotlin.timetravel.client.internal.settings

class SettingsConfig(
    val defaults: Defaults
) {
    class Defaults(
        val connectViaAdb: Boolean
    )
}
