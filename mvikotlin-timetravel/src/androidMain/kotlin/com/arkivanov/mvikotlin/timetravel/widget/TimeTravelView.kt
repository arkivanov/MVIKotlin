package com.arkivanov.mvikotlin.timetravel.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.timetravel.R
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController

/**
 * Provides time travel controls like (record, stop, step back and forward, etc.) and displays list of recorded events.
 * Tap on event to show its details. Tap on Bug icon to debug the event. Uses [TimeTravelController].
 */
class TimeTravelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        View.inflate(context, R.layout.view_time_travel, this)
    }

    private val recyclerView = findViewById<RecyclerView>(R.id.recycler_time_travel_events)
    private val recordButton = findViewById<View>(R.id.button_time_travel_record)
    private val stopButton = findViewById<View>(R.id.button_time_travel_stop)
    private val moveToStartButton = findViewById<View>(R.id.button_time_travel_move_to_start)
    private val stepBackwardButton = findViewById<View>(R.id.button_time_travel_step_backward)
    private val stepForwardButton = findViewById<View>(R.id.button_time_travel_step_forward)
    private val moveToEndButton = findViewById<View>(R.id.button_time_travel_move_to_end)
    private val cancelButton = findViewById<View>(R.id.button_time_travel_cancel)
    private val exportButton = findViewById<View>(R.id.button_time_travel_export)
    private val importButton = findViewById<View>(R.id.button_time_travel_import)

    private val adapter = TimeTravelEventAdapter()
    private var disposable: Disposable? = null

    init {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        recordButton.setOnClickListener { timeTravelController.startRecording() }
        stopButton.setOnClickListener { timeTravelController.stopRecording() }
        moveToStartButton.setOnClickListener { timeTravelController.moveToStart() }
        stepBackwardButton.setOnClickListener { timeTravelController.stepBackward() }
        stepForwardButton.setOnClickListener { timeTravelController.stepForward() }
        moveToEndButton.setOnClickListener { timeTravelController.moveToEnd() }
        cancelButton.setOnClickListener { timeTravelController.cancel() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        disposable =
            timeTravelController.states(
                observer {
                    adapter.setEvents(it.events, it.selectedEventIndex)
                    recyclerView.scrollToPosition(it.selectedEventIndex + 1)
                    updateControlsVisibility(it.mode)
                }
            )
    }

    override fun onDetachedFromWindow() {
        disposable?.dispose()
        disposable = null

        super.onDetachedFromWindow()
    }

    fun setOnExportEventsListener(listener: (() -> Unit)?) {
        exportButton.setOnClickListener(listener)
        updateControlsVisibility()
    }

    fun setOnImportEventsListener(listener: (() -> Unit)?) {
        importButton.setOnClickListener(listener)
        updateControlsVisibility()
    }

    private fun updateControlsVisibility() {
        updateControlsVisibility(timeTravelController.state.mode)
    }

    private fun updateControlsVisibility(mode: TimeTravelState.Mode) {
        when (mode) {
            TimeTravelState.Mode.IDLE -> setControlsVisibility(record = true, import = true)
            TimeTravelState.Mode.RECORDING -> setControlsVisibility(stop = true, cancel = true)
            TimeTravelState.Mode.STOPPED -> setControlsVisibility(next = true, prev = true, cancel = true, export = true)
        }.let {}
    }

    private fun setControlsVisibility(
        record: Boolean = false,
        stop: Boolean = false,
        prev: Boolean = false,
        next: Boolean = false,
        cancel: Boolean = false,
        export: Boolean = false,
        import: Boolean = false
    ) {
        recordButton.isVisible = record
        stopButton.isVisible = stop
        moveToStartButton.isVisible = prev
        stepBackwardButton.isVisible = prev
        stepForwardButton.isVisible = next
        moveToEndButton.isVisible = next
        cancelButton.isVisible = cancel
        exportButton.isVisible = export && exportButton.hasOnClickListeners()
        importButton.isVisible = import && importButton.hasOnClickListeners()
    }
}
