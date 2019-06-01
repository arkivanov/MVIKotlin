package com.arkivanov.mvidroid.widget

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.arkivanov.mvidroid.debug.R
import com.arkivanov.mvidroid.store.MviEventType
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelController
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelEvent
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelEvents
import com.arkivanov.mvidroid.store.timetravel.MviTimeTravelState
import com.arkivanov.mvidroid.utils.DeepStringMode
import com.arkivanov.mvidroid.utils.toDeepString
import io.reactivex.disposables.CompositeDisposable
import java.util.IdentityHashMap

/**
 * Provides time travel controls like (record, stop, step back and forward, etc.) and displays list of recorded events.
 * Tap on event to show its details. Tap on Bug icon to debug the event. Uses [MviTimeTravelController].
 * Used by [MviTimeTravelDrawer].
 */
class MviTimeTravelView @JvmOverloads constructor(
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
    private val adapter = Adapter()
    private var disposables = CompositeDisposable()

    init {
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        recordButton.setOnClickListener { MviTimeTravelController.startRecording() }
        stopButton.setOnClickListener { MviTimeTravelController.stop() }
        moveToStartButton.setOnClickListener { MviTimeTravelController.moveToStart() }
        stepBackwardButton.setOnClickListener { MviTimeTravelController.stepBackward() }
        stepForwardButton.setOnClickListener { MviTimeTravelController.stepForward() }
        moveToEndButton.setOnClickListener { MviTimeTravelController.moveToEnd() }
        cancelButton.setOnClickListener { MviTimeTravelController.cancel() }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val view = getChildAt(0)
        removeViewAt(0)
        addView(view)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        MviTimeTravelController
            .eventsUpdates
            .subscribe {
                adapter.setEvents(it)
                recyclerView.scrollToPosition(it.index + 1)
            }
            .also { disposables.add(it) }

        MviTimeTravelController
            .stateUpdates
            .distinctUntilChanged()
            .subscribe { updateControlsVisibility(it) }
            .also { disposables.add(it) }
    }

    override fun onDetachedFromWindow() {
        disposables.clear()

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
        updateControlsVisibility(MviTimeTravelController.state)
    }

    private fun updateControlsVisibility(state: MviTimeTravelState) {
        when (state) {
            MviTimeTravelState.IDLE -> setControlsVisibility(record = true, import = true)
            MviTimeTravelState.RECORDING -> setControlsVisibility(stop = true, cancel = true)
            MviTimeTravelState.STOPPED -> setControlsVisibility(next = true, prev = true, cancel = true, export = true)
        }.also {}
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
        recordButton.setVisible(record)
        stopButton.setVisible(stop)
        moveToStartButton.setVisible(prev)
        stepBackwardButton.setVisible(prev)
        stepForwardButton.setVisible(next)
        moveToEndButton.setVisible(next)
        cancelButton.setVisible(cancel)
        exportButton.setVisible(export && exportButton.hasOnClickListeners())
        importButton.setVisible(import && importButton.hasOnClickListeners())
    }

    private companion object {
        private fun View.setVisible(isVisible: Boolean) {
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }

        private fun View.setOnClickListener(listener: (() -> Unit)?) {
            setOnClickListener(
                listener?.let { callback ->
                    OnClickListener { callback() }
                }
            )
        }
    }

    private class Adapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val eventsMap = IdentityHashMap<MviTimeTravelEvent, String>()
        private var events = MviTimeTravelEvents()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            LayoutInflater
                .from(parent.context).inflate(viewType, parent, false)
                .let { view ->
                    when (viewType) {
                        LAYOUT_EVENT -> EventViewHolder(view)
                        LAYOUT_SEPARATOR -> SeparatorViewHolder(view)
                        else -> throw IllegalStateException("Unsupported view type: $viewType")
                    }
                }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as? EventViewHolder)?.bind(events.getEvent(position)!!)
        }

        override fun getItemCount(): Int = events.items.size + 1

        override fun getItemViewType(position: Int): Int = if (events.getEvent(position) == null) LAYOUT_SEPARATOR else LAYOUT_EVENT

        fun setEvents(events: MviTimeTravelEvents) {
            val oldEvents = this.events
            this.events = events

            DiffUtil
                .calculateDiff(DiffCallback(oldEvents, events), true)
                .dispatchUpdatesTo(this)
        }

        private companion object {
            private val LAYOUT_EVENT = R.layout.item_time_travel_event
            private val LAYOUT_SEPARATOR = R.layout.item_time_travel_separator

            private fun MviTimeTravelEvents.getEvent(position: Int): MviTimeTravelEvent? =
                when {
                    position <= index -> items[position]
                    position > index + 1 -> items[position - 1]
                    else -> null
                }
        }

        private inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val debugButton = itemView.findViewById<View>(R.id.button_debug)
            private val storeNameTextView = itemView.findViewById<TextView>(R.id.text_store_name)
            private val eventNameTextView = itemView.findViewById<TextView>(R.id.text_event_name)
            private val eventValueTextView = itemView.findViewById<TextView>(R.id.text_event_value)
            private lateinit var boundEvent: MviTimeTravelEvent

            init {
                itemView.setOnClickListener {
                    AlertDialog.Builder(itemView.context)
                        .setTitle(boundEvent.value::class.java.simpleName)
                        .setMessage(boundEvent.value.toDeepString(DeepStringMode.FULL, true))
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
                }

                debugButton.setOnClickListener { MviTimeTravelController.debugEvent(boundEvent) }
            }

            @SuppressLint("SetTextI18n")
            fun bind(event: MviTimeTravelEvent) {
                boundEvent = event
                debugButton.setVisible(event.type !== MviEventType.STATE)
                storeNameTextView.text = event.storeName
                eventNameTextView.text = "${event.value::class.java.simpleName} (${event.type.name})"
                eventValueTextView.text =
                    eventsMap[event] ?: event.value.toDeepString(DeepStringMode.SHORT, false).also { eventsMap[event] = it }
            }
        }

        private class SeparatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        private class DiffCallback(
            private val oldEvents: MviTimeTravelEvents,
            private val newEvents: MviTimeTravelEvents
        ) : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldEvents.items.size + 1

            override fun getNewListSize(): Int = newEvents.items.size + 1

            override fun areItemsTheSame(oldIndex: Int, newIndex: Int): Boolean =
                oldEvents.getEvent(oldIndex) === newEvents.getEvent(newIndex)

            override fun areContentsTheSame(oldIndex: Int, newIndex: Int): Boolean =
                oldEvents.getEvent(oldIndex) == newEvents.getEvent(newIndex)
        }
    }
}