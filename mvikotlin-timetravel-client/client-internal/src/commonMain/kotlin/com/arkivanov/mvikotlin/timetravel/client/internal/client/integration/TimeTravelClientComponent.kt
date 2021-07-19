package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.adbcontroller.AdbController
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.mappers.stateToModel
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.Label
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.SettingsConfig
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.integration.TimeTravelSettingsComponent
import com.arkivanov.mvikotlin.timetravel.client.internal.utils.mapState
import com.badoo.reaktive.subject.behavior.BehaviorObservable
import com.russhwolf.settings.Settings

class TimeTravelClientComponent(
    lifecycle: Lifecycle,
    storeFactory: StoreFactory,
    settingsFactory: Settings.Factory,
    settingsConfig: SettingsConfig,
    private val adbController: AdbController,
    private val onImportEvents: () -> ByteArray?,
    private val onExportEvents: (ByteArray) -> Unit
) : TimeTravelClient {

    override val settings: TimeTravelSettings =
        TimeTravelSettingsComponent(
            lifecycle = lifecycle,
            storeFactory = storeFactory,
            settingsFactory = settingsFactory,
            settingsConfig = settingsConfig
        )

    private val store =
        TimeTravelClientStoreFactory(
            storeFactory = storeFactory,
            connector = TimeTravelClientStoreConnector(
                host = { getSettings().host },
                port = { getSettings().port }
            ),
        ).create()

    override val models: BehaviorObservable<TimeTravelClient.Model> = store.mapState(lifecycle, stateToModel)

    init {
        bind(lifecycle, BinderLifecycleMode.CREATE_DESTROY) {
            store.labels bindTo ::onLabel
        }
    }

    private fun onLabel(label: Label): Unit =
        when (label) {
            is Label.ExportEvents -> onExportEvents(label.data)
        }

    override fun onConnectClicked() {
        val settings = getSettings()

        if (settings.connectViaAdb) {
            when (val result = adbController.forwardPort(port = settings.port)) {
                is AdbController.Result.Success -> store.accept(Intent.Connect)
                is AdbController.Result.Error -> store.accept(Intent.RaiseError(errorText = result.text))
            }.let {}
        } else {
            store.accept(Intent.Connect)
        }
    }

    override fun onDisconnectClicked() {
        store.accept(Intent.Disconnect)
    }

    override fun onStartRecordingClicked() {
        store.accept(Intent.StartRecording)
    }

    override fun onStopRecordingClicked() {
        store.accept(Intent.StopRecording)
    }

    override fun onMoveToStartClicked() {
        store.accept(Intent.MoveToStart)
    }

    override fun onStepBackwardClicked() {
        store.accept(Intent.StepBackward)
    }

    override fun onStepForwardClicked() {
        store.accept(Intent.StepForward)
    }

    override fun onMoveToEndClicked() {
        store.accept(Intent.MoveToEnd)
    }

    override fun onCancelClicked() {
        store.accept(Intent.Cancel)
    }

    override fun onDebugEventClicked() {
        store.accept(Intent.DebugEvent)
    }

    override fun onEditSettingsClicked() {
        settings.onEditClicked()
    }

    override fun onEventSelected(index: Int) {
        store.accept(Intent.SelectEvent(index = index))
    }

    override fun onExportEventsClicked() {
        store.accept(Intent.ExportEvents)
    }

    override fun onImportEventsClicked() {
        onImportEvents()?.also {
            store.accept(Intent.ImportEvents(data = it))
        }
    }

    override fun onDismissErrorClicked() {
        store.accept(Intent.DismissError)
    }

    private fun getSettings(): TimeTravelSettings.Model.Settings =
        settings.models.value.settings
}
