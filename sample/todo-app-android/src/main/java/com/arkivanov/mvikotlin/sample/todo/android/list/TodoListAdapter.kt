package com.arkivanov.mvikotlin.sample.todo.android.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoItem
import com.arkivanov.mvikotlin.sample.todo.android.R

class TodoListAdapter(
    private val listener: Listener
) : RecyclerView.Adapter<TodoListAdapter.ViewHolder>() {

    private var items: List<TodoItem> = emptyList()

    fun setItems(items: List<TodoItem>) {
        val oldItems = this.items
        this.items = items
        diff(oldItems, items, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false), listener)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private companion object {
        private fun diff(oldItems: List<TodoItem>, newItems: List<TodoItem>, adapter: TodoListAdapter) {
            DiffUtil
                .calculateDiff(
                    object : DiffUtil.Callback() {
                        override fun getOldListSize(): Int = oldItems.size

                        override fun getNewListSize(): Int = newItems.size

                        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            oldItems[oldItemPosition].id == newItems[newItemPosition].id

                        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            oldItems[oldItemPosition] == newItems[newItemPosition]
                    }
                )
                .dispatchUpdatesTo(adapter)
        }
    }

    interface Listener {
        fun onItemClick(id: String)

        fun onItemDoneClick(id: String)

        fun onItemDeleteClick(id: String)
    }

    class ViewHolder(view: View, listener: Listener) : RecyclerView.ViewHolder(view) {
        private lateinit var boundItem: TodoItem

        init {
            itemView.setOnClickListener {
                listener.onItemClick(boundItem.id)
            }
        }

        private val onCheckedChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
            listener.onItemDoneClick(boundItem.id)
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
            checkBox.isChecked = item.data.isDone
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
            textView.text = item.data.text
        }
    }
}
