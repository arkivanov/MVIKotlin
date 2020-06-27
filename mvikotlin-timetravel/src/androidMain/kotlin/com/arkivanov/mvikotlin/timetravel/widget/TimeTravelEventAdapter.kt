package com.arkivanov.mvikotlin.timetravel.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.store.StoreEventType
import com.arkivanov.mvikotlin.timetravel.R
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.getFullTypeName

internal class TimeTravelEventAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        private val eventValueTextView = itemView.findViewById<TextView>(R.id.text_event_value)
        private lateinit var boundEvent: TimeTravelEvent

        init {
            itemView.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle(boundEvent.text)
                    .setMessage(R.string.mvi_time_travel_loading)
                    .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .show()
                    .also { dialog -> AsyncValueParser.parse(boundEvent.value, dialog::setMessage) }
            }

            debugButton.setOnClickListener { timeTravelController.debugEvent(boundEvent.id) }
        }

        @SuppressLint("SetTextI18n")
        fun bind(event: TimeTravelEvent) {
            boundEvent = event
            debugButton.isVisible = event.type !== StoreEventType.STATE
            storeNameTextView.text = event.storeName
            eventValueTextView.text = event.text
        }

        private val TimeTravelEvent.text: String get() = "${type.title}.${getFullTypeName(value)}"
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
