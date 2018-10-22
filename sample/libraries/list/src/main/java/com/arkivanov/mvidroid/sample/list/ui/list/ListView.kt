package com.arkivanov.mvidroid.sample.list.ui.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.arkivanov.mvidroid.sample.list.R
import com.arkivanov.mvidroid.sample.list.component.ListUiEvent
import com.arkivanov.mvidroid.view.MviBaseView

internal class ListView(root: View) : MviBaseView<ListViewModel, ListUiEvent>() {

    private val adapter =
        ListAdapter(
            object : ListAdapter.Listener {
                override fun onItemClick(id: Long) {
                    dispatch(ListUiEvent.OnItemSelected(id))
                }

                override fun onItemCheckedChanged(id: Long, isChecked: Boolean) {
                    dispatch(ListUiEvent.OnSetItemCompleted(id, isChecked))
                }

                override fun onItemDeleteClick(id: Long) {
                    dispatch(ListUiEvent.OnDeleteItem(id))
                }
            }
        )

    private val todoEditText = root.findViewById<EditText>(R.id.todo_edit)

    init {
        root.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = this@ListView.adapter
        }

        root.findViewById<View>(R.id.add_button).setOnClickListener {
            dispatch(ListUiEvent.OnAddItem(todoEditText.text.toString()))
            todoEditText.text = null
        }

        registerDiffByReference(adapter, ListViewModel::items) { items = it }
    }
}