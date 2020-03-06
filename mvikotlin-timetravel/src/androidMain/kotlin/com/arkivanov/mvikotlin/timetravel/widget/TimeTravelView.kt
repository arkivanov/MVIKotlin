package com.arkivanov.mvikotlin.timetravel.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.timetravel.R
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.controller.TimeTravelController
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.utils.internal.DeepStringMode
import com.arkivanov.mvikotlin.utils.internal.toDeepString
import java.util.IdentityHashMap

/**
 * Provides time travel controls like (record, stop, step back and forward, etc.) and displays list of recorded events.
 * Tap on event to show its details. Tap on Bug icon to debug the event. Uses [TimeTravelController].
 */
class TimeTravelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    // FIXME: Fix all commented code

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

    private val adapter = Adapter()
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

//    override fun onFinishInflate() {
//        super.onFinishInflate()
//
//        val view = getChildAt(0)
//        removeViewAt(0)
//        addView(view)
//    }

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

    private class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val eventsMap = IdentityHashMap<TimeTravelEvent, String>()
        private var events = Events()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            LayoutInflater
                .from(parent.context).inflate(viewType, parent, false)
                .let { view ->
                    when (viewType) {
                        LAYOUT_EVENT -> EventViewHolder(view)
                        LAYOUT_SEPARATOR -> SeparatorViewHolder(
                            view
                        )
                        else -> throw IllegalStateException("Unsupported view type: $viewType")
                    }
                }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as? EventViewHolder)?.bind(events.get(position)!!)
        }

        override fun getItemCount(): Int = events.items.size + 1

        override fun getItemViewType(position: Int): Int = if (events.get(position) == null) LAYOUT_SEPARATOR else LAYOUT_EVENT

        fun setEvents(events: List<TimeTravelEvent>, selectedEventIndex: Int) {
            val oldEvents = this.events
            val newEvents = Events(
                items = events,
                index = selectedEventIndex
            )
            this.events = newEvents

            DiffUtil
                .calculateDiff(
                    DiffCallback(
                        oldEvents,
                        newEvents
                    ), true
                )
                .dispatchUpdatesTo(this)
        }

        private companion object {
            private val LAYOUT_EVENT = R.layout.item_time_travel_event
            private val LAYOUT_SEPARATOR = R.layout.item_time_travel_separator
        }

        private inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val debugButton = itemView.findViewById<View>(R.id.button_debug)
            private val storeNameTextView = itemView.findViewById<TextView>(R.id.text_store_name)
            private val eventNameTextView = itemView.findViewById<TextView>(R.id.text_event_name)
            private val eventValueTextView = itemView.findViewById<TextView>(R.id.text_event_value)
            private lateinit var boundEvent: TimeTravelEvent

            init {
                itemView.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle(boundEvent.value::class.java.simpleName)
                        .setMessage(boundEvent.value.toDeepString(DeepStringMode.FULL, true))
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
                }

                debugButton.setOnClickListener { timeTravelController.debugEvent(boundEvent) }
            }

            @SuppressLint("SetTextI18n")
            fun bind(event: TimeTravelEvent) {
                boundEvent = event
                debugButton.isVisible = event.type !== StoreEventType.STATE
                storeNameTextView.text = event.storeName
                eventNameTextView.text = "${event.value::class.java.simpleName} (${event.type.name})"
                eventValueTextView.text =
                    eventsMap[event] ?: event.value.toDeepString(DeepStringMode.SHORT, false).also { eventsMap[event] = it }
            }
        }

        private class SeparatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        private class DiffCallback(
            private val oldEvents: Events,
            private val newEvents: Events
        ) : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldEvents.items.size + 1

            override fun getNewListSize(): Int = newEvents.items.size + 1

            override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean =
                oldEvents.get(oldIndex) === newEvents.get(newIndex)

            override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean =
                oldEvents.get(oldIndex) == newEvents.get(newIndex)
        }

        private class Events(
            val items: List<TimeTravelEvent> = emptyList(),
            val index: Int = -1
        ) {
            fun get(position: Int): TimeTravelEvent? =
                when {
                    position <= index -> items[position]
                    position > index + 1 -> items[position - 1]
                    else -> null
                }
        }
    }
}
