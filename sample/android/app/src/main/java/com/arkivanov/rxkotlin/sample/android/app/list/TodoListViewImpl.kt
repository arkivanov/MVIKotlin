package com.arkivanov.rxkotlin.sample.android.app.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.main.view.AbstractView
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.shared.view.TodoListView.Model
import com.arkivanov.rxkotlin.sample.android.app.R

class TodoListViewImpl(
    root: View,
    private val onItemSelected: (id: String) -> Unit
) : AbstractView<Model, Event>(), TodoListView {

    private val adapter =
        TodoListAdapter(
            object : TodoListAdapter.Listener {
                override fun onItemClick(id: String) {
                    dispatch(Event.ItemClicked(id))
                }

                override fun onItemDoneClick(id: String) {
                    dispatch(Event.ItemDoneClicked(id))
                }

                override fun onItemDeleteClick(id: String) {
                    dispatch(Event.ItemDeleteClicked(id))
                }
            }
        )

    private val renderer =
        diff<Model> {
            diff(get = Model::items, bind = adapter::setItems)

            diff(get = Model::selectedItemId) {
                if (it != null) {
                    dispatch(Event.ItemSelectionHandled)
                    onItemSelected(it)
                }
            }
        }

    init {
        requireNotNull(root.findViewById<RecyclerView>(R.id.recycler_view)).adapter = adapter
    }

    override fun render(model: Model) {
        renderer.render(model)
    }
}
