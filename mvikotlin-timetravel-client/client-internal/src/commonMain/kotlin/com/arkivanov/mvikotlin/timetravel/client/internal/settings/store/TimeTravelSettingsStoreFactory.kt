package com.arkivanov.mvikotlin.timetravel.client.internal.settings.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.ReaktiveExecutor
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.store.TimeTravelSettingsStore.State

internal class TimeTravelSettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val settings: Settings
) {

    fun create(): TimeTravelSettingsStore =
        object : TimeTravelSettingsStore, Store<Intent, State, Nothing> by storeFactory.create(
            initialState = State(settings = settings.settings),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        object EditRequested : Msg()
        data class EditFinished(val newSettings: State.Settings) : Msg()
        object EditCancelled : Msg()
        data class HostChanged(val host: String) : Msg()
        data class PortChanged(val port: String) : Msg()
        data class ConnectViaAdbChanged(val connectViaAdb: Boolean) : Msg()
        data class WrapEventDetailsChanged(val wrapEventDetails: Boolean) : Msg()
        data class DarkModeChanged(val isDarkMode: Boolean) : Msg()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Msg, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.StartEdit -> dispatch(Msg.EditRequested)
                is Intent.SaveEdit -> saveEdit(state = getState())
                is Intent.CancelEdit -> dispatch(Msg.EditCancelled)
                is Intent.SetHost -> dispatch(Msg.HostChanged(host = intent.host))
                is Intent.SetPort -> dispatch(Msg.PortChanged(port = intent.port))
                is Intent.SetConnectViaAdb -> dispatch(Msg.ConnectViaAdbChanged(connectViaAdb = intent.connectViaAdb))
                is Intent.SetWrapEventDetails -> dispatch(Msg.WrapEventDetailsChanged(wrapEventDetails = intent.wrapEventDetails))
                is Intent.SetDarkMode -> dispatch(Msg.DarkModeChanged(isDarkMode = intent.isDarkMode))
            }

        private fun saveEdit(state: State) {
            val newSettings = state.editing?.toSettings(state.settings) ?: return
            settings.settings = newSettings
            dispatch(Msg.EditFinished(newSettings = newSettings))
        }

        private fun State.Editing.toSettings(oldSettings: State.Settings): State.Settings? {
            return State.Settings(
                host = host,
                port = port.toIntOrNull() ?: return null,
                connectViaAdb = connectViaAdb,
                wrapEventDetails = wrapEventDetails,
                isDarkMode = isDarkMode ?: oldSettings.isDarkMode
            )
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State =
            when (msg) {
                is Msg.EditRequested -> copy(editing = settings.toEditing())
                is Msg.EditFinished -> copy(settings = msg.newSettings, editing = null)
                is Msg.EditCancelled -> copy(editing = null)
                is Msg.HostChanged -> copy(editing = editing?.copy(host = msg.host))
                is Msg.PortChanged -> copy(editing = editing?.copy(port = msg.port))
                is Msg.ConnectViaAdbChanged -> copy(editing = editing?.copy(connectViaAdb = msg.connectViaAdb))
                is Msg.WrapEventDetailsChanged -> copy(editing = editing?.copy(wrapEventDetails = msg.wrapEventDetails))
                is Msg.DarkModeChanged -> copy(editing = editing?.copy(isDarkMode = msg.isDarkMode))
            }

        private fun State.Settings.toEditing(): State.Editing =
            State.Editing(
                host = host,
                port = port.toString(),
                connectViaAdb = connectViaAdb,
                wrapEventDetails = wrapEventDetails,
                isDarkMode = isDarkMode
            )
    }

    interface Settings {
        var settings: State.Settings
    }
}
