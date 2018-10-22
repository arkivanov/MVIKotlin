package com.arkivanov.mvidroid.sample.list.ui.list

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.arkivanov.mvidroid.sample.list.R
import com.arkivanov.mvidroid.sample.list.model.TodoItem

internal class ListAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<ListAdapter.Holder>() {

    var items: List<TodoItem> = emptyList()
        set(value) {
            val oldItems = items
            field = value
            processDiff(oldItems, value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.layout_todo_item, parent, false), listener)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    private fun processDiff(oldItems: List<TodoItem>, newItems: List<TodoItem>) {
        DiffUtil
            .calculateDiff(
                object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int = oldItems.size

                    override fun getNewListSize(): Int = newItems.size

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        oldItems[oldItemPosition].id == newItems[newItemPosition].id

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        oldItems[oldItemPosition] == newItems[newItemPosition]
                },
                false
            )
            .dispatchUpdatesTo(this)
    }

    interface Listener {
        fun onItemClick(id: Long)

        fun onItemCheckedChanged(id: Long, isChecked: Boolean)

        fun onItemDeleteClick(id: Long)
    }

    class Holder(itemView: View, listener: Listener) : RecyclerView.ViewHolder(itemView) {
        private lateinit var boundItem: TodoItem

        init {
            itemView.setOnClickListener {
                listener.onItemClick(boundItem.id)
            }
        }

        private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
            listener.onItemCheckedChanged(boundItem.id, isChecked)
        }
        private val checkBox = itemView.findViewById<CheckBox>(R.id.check_box).apply {
            setOnCheckedChangeListener(onCheckedChangeListener)
        }

        private val textView = itemView.findViewById<TextView>(R.id.text)

        init {
            itemView.findViewById<View>(R.id.delete_button).apply {
                setOnClickListener {
                    listener.onItemDeleteClick(boundItem.id)
                }
            }
        }

        fun bind(item: TodoItem) {
            boundItem = item
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = item.isCompleted
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
            textView.text = item.text
        }
    }
}
