package com.arkivanov.mvikotlin.timetravel.client.internal.client.integration

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.arkivanov.mvikotlin.timetravel.client.internal.client.Connector
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.integration.mappers.stateToModel
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.Intent
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStore.Label
import com.arkivanov.mvikotlin.timetravel.client.internal.client.store.TimeTravelClientStoreFactory
import com.arkivanov.mvikotlin.timetravel.client.internal.utils.mapState
import com.badoo.reaktive.subject.behavior.BehaviorObservable

class TimeTravelClientComponent(
    lifecycle: Lifecycle,
    storeFactory: StoreFactory,
    connector: Connector,
    private val onImportEvents: () -> ByteArray?,
    private val onExportEvents: (ByteArray) -> Unit
) : TimeTravelClient {

    private val store =
        TimeTravelClientStoreFactory(
            storeFactory = storeFactory,
            connector = connector,
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
        store.accept(Intent.Connect)
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
}
