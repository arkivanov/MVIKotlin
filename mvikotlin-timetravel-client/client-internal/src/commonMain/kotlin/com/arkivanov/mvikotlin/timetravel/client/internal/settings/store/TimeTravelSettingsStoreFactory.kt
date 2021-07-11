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

    private sealed class Result {
        object EditRequested : Result()
        data class EditFinished(val newSettings: State.Settings) : Result()
        object EditCancelled : Result()
        data class HostChanged(val host: String) : Result()
        data class PortChanged(val port: String) : Result()
        data class ConnectViaAdbChanged(val connectViaAdb: Boolean) : Result()
        data class WrapEventDetailsChanged(val wrapEventDetails: Boolean) : Result()
        data class DarkModeChanged(val isDarkMode: Boolean) : Result()
    }

    private inner class ExecutorImpl : ReaktiveExecutor<Intent, Nothing, State, Result, Nothing>() {
        override fun executeIntent(intent: Intent, getState: () -> State): Unit =
            when (intent) {
                is Intent.StartEdit -> dispatch(Result.EditRequested)
                is Intent.SaveEdit -> saveEdit(state = getState())
                is Intent.CancelEdit -> dispatch(Result.EditCancelled)
                is Intent.SetHost -> dispatch(Result.HostChanged(host = intent.host))
                is Intent.SetPort -> dispatch(Result.PortChanged(port = intent.port))
                is Intent.SetConnectViaAdb -> dispatch(Result.ConnectViaAdbChanged(connectViaAdb = intent.connectViaAdb))
                is Intent.SetWrapEventDetails -> dispatch(Result.WrapEventDetailsChanged(wrapEventDetails = intent.wrapEventDetails))
                is Intent.SetDarkMode -> dispatch(Result.DarkModeChanged(isDarkMode = intent.isDarkMode))
            }

        private fun saveEdit(state: State) {
            val newSettings = state.editing?.toSettings(state.settings) ?: return
            settings.settings = newSettings
            dispatch(Result.EditFinished(newSettings = newSettings))
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

    private object ReducerImpl : Reducer<State, Result> {
        override fun State.reduce(result: Result): State =
            when (result) {
                is Result.EditRequested -> copy(editing = settings.toEditing())
                is Result.EditFinished -> copy(settings = result.newSettings, editing = null)
                is Result.EditCancelled -> copy(editing = null)
                is Result.HostChanged -> copy(editing = editing?.copy(host = result.host))
                is Result.PortChanged -> copy(editing = editing?.copy(port = result.port))
                is Result.ConnectViaAdbChanged -> copy(editing = editing?.copy(connectViaAdb = result.connectViaAdb))
                is Result.WrapEventDetailsChanged -> copy(editing = editing?.copy(wrapEventDetails = result.wrapEventDetails))
                is Result.DarkModeChanged -> copy(editing = editing?.copy(isDarkMode = result.isDarkMode))
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
