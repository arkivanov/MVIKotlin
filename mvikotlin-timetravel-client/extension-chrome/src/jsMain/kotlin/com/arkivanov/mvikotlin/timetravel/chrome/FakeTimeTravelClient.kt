package com.arkivanov.mvikotlin.timetravel.chrome

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient.Model
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import com.badoo.reaktive.subject.behavior.BehaviorSubject

internal class FakeTimeTravelClient : TimeTravelClient {

    override val models: BehaviorSubject<Model> =
        BehaviorSubject(
            Model(
                events = List(100) { "Some event $it " },
                currentEventIndex = 1,
                buttons = Model.Buttons(
                    isConnectEnabled = false,
                    isDisconnectEnabled = false,
                    isStartRecordingEnabled = false,
                    isStopRecordingEnabled = false,
                    isMoveToStartEnabled = false,
                    isStepBackwardEnabled = false,
                    isStepForwardEnabled = false,
                    isMoveToEndEnabled = false,
                    isCancelEnabled = false,
                    isDebugEventEnabled = false,
                    isExportEventsEnabled = false,
                    isImportEventsEnabled = false,
                ),
                selectedEventIndex = 2,
                selectedEventValue = ValueParser().parseValue(Data()),
                errorText = null,
            )
        )

    override fun onConnectClicked() { /* no-op */ }

    override fun onDisconnectClicked() { /* no-op */ }

    override fun onStartRecordingClicked() { /* no-op */ }

    override fun onStopRecordingClicked() { /* no-op */ }

    override fun onMoveToStartClicked() { /* no-op */ }

    override fun onStepBackwardClicked() { /* no-op */ }

    override fun onStepForwardClicked() { /* no-op */ }

    override fun onMoveToEndClicked() { /* no-op */ }

    override fun onCancelClicked() { /* no-op */ }

    override fun onDebugEventClicked() { /* no-op */ }

    override fun onEventSelected(index: Int) {
        updateModel { it.copy(selectedEventIndex = index) }
    }

    override fun onExportEventsClicked() { /* no-op */ }

    override fun onImportEventsClicked() { /* no-op */ }

    override fun onDismissErrorClicked() { /* no-op */ }

    private fun updateModel(update: (Model) -> Model) {
        models.onNext(update(models.value))
    }

    private data class Data(
        val count: Int = 5,
        val title: String = "Some string ".repeat(10),
        val items: List<String> = listOf("1", "2", "3"),
        val innerData: Data? = Data(innerData = null),
    )
}
