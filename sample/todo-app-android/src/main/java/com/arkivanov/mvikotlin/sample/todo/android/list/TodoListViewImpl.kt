package com.arkivanov.mvikotlin.sample.todo.android.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.android.R
import com.arkivanov.mvikotlin.sample.todo.android.getViewById
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView.Model

class TodoListViewImpl(root: View) : BaseMviView<Model, Event>(), TodoListView {

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

    override val renderer =
        diff<Model> {
            diff(get = Model::items, compare = { a, b -> a === b }, set = adapter::setItems)
        }

    init {
        root.getViewById<RecyclerView>(R.id.recycler_view).adapter = adapter
    }
}
