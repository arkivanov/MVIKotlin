package com.arkivanov.mvikotlin.sample.coroutines.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.sample.coroutines.app.R
import com.arkivanov.mvikotlin.sample.coroutines.app.getViewById
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Model.Item
import kotlin.properties.Delegates.observable

class ListAdapter(
    private val onItemClick: (id: String) -> Unit,
    private val onItemDoneClick: (id: String) -> Unit,
    private val onItemDeleteClick: (id: String) -> Unit,
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var items: List<Item> by observable(emptyList()) { _, old, new -> diff(old, new, this) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private companion object {
        private fun diff(oldItems: List<Item>, newItems: List<Item>, adapter: ListAdapter) {
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var boundItem: Item

        init {
            itemView.setOnClickListener { onItemClick(boundItem.id) }
        }

        private val onCheckedChangeListener = OnCheckedChangeListener { _, _ -> onItemDoneClick(boundItem.id) }

        private val checkBox =
            itemView.getViewById<CheckBox>(R.id.check_box).apply {
                setOnCheckedChangeListener(onCheckedChangeListener)
            }

        private val textView = itemView.getViewById<TextView>(R.id.text)

        init {
            itemView.getViewById<View>(R.id.delete_button).setOnClickListener {
                onItemDeleteClick(boundItem.id)
            }
        }

        fun bind(item: Item) {
            boundItem = item
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = item.isDone
            checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
            textView.text = item.text
        }
    }
}
