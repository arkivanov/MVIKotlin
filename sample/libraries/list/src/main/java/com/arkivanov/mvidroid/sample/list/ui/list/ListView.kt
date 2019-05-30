package com.arkivanov.mvidroid.sample.list.ui.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.arkivanov.mvidroid.sample.list.R
import com.arkivanov.mvidroid.utils.diffByReference
import com.arkivanov.mvidroid.view.MviBaseView

internal class ListView(root: View) : MviBaseView<ListViewModel, ListView.Event>() {

    private val adapter =
        ListAdapter(
            object : ListAdapter.Listener {
                override fun onItemClick(id: Long) {
                    dispatch(Event.OnItemSelected(id))
                }

                override fun onItemCheckedChanged(id: Long, isChecked: Boolean) {
                    dispatch(Event.OnSetItemCompleted(id, isChecked))
                }

                override fun onItemDeleteClick(id: Long) {
                    dispatch(Event.OnDeleteItem(id))
                }
            }
        )

    private val todoEditText = root.findViewById<EditText>(R.id.todo_edit)

    init {
        root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = this@ListView.adapter
        }

        root.findViewById<View>(R.id.add_button).setOnClickListener {
            dispatch(Event.OnAddItem(todoEditText.text.toString()))
            todoEditText.text = null
        }

        diff.diffByReference(ListViewModel::items, adapter::items::set)
    }

    sealed class Event {
        class OnAddItem(val text: String) : Event()
        class OnSetItemCompleted(val id: Long, val isCompleted: Boolean) : Event()
        class OnDeleteItem(val id: Long) : Event()
        class OnItemSelected(val id: Long) : Event()
    }
}
