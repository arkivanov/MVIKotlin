package com.arkivanov.mvidroid.sample.list.ui.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.arkivanov.mvidroid.sample.list.R
import com.arkivanov.mvidroid.sample.list.component.ListEvent
import com.arkivanov.mvidroid.view.MviBaseView
import com.arkivanov.mvidroid.view.registerDiffByReference

internal class ListView(root: View) : MviBaseView<ListViewModel, ListEvent>() {

    private val adapter =
        ListAdapter(
            object : ListAdapter.Listener {
                override fun onItemClick(id: Long) {
                    dispatch(ListEvent.OnItemSelected(id))
                }

                override fun onItemCheckedChanged(id: Long, isChecked: Boolean) {
                    dispatch(ListEvent.OnSetItemCompleted(id, isChecked))
                }

                override fun onItemDeleteClick(id: Long) {
                    dispatch(ListEvent.OnDeleteItem(id))
                }
            }
        )

    private val todoEditText = root.findViewById<EditText>(R.id.todo_edit)

    init {
        root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = this@ListView.adapter
        }

        root.findViewById<View>(R.id.add_button).setOnClickListener {
            dispatch(ListEvent.OnAddItem(todoEditText.text.toString()))
            todoEditText.text = null
        }

        registerDiffByReference(ListViewModel::items) { adapter.items = it }
    }
}