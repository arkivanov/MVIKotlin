package com.arkivanov.mvidroid.sample.ui.list

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.EditText
import com.arkivanov.mvidroid.sample.R
import com.arkivanov.mvidroid.sample.component.list.ListUiEvent
import com.arkivanov.mvidroid.sample.ui.details.DetailsActivity
import com.arkivanov.mvidroid.sample.ui.plusAssign
import com.arkivanov.mvidroid.utils.mapNotNull
import com.arkivanov.mvidroid.view.MviAbstractView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class ListView(
    private val activity: Activity
) : MviAbstractView<ListViewModel, ListUiEvent>() {

    private val adapter =
        ListAdapter(
            object : ListAdapter.Listener {
                override fun onItemClick(id: Long) {
                    dispatch(ListUiEvent.OnItemClick(id))
                }

                override fun onItemCheckedChanged(id: Long, isChecked: Boolean) {
                    dispatch(ListUiEvent.OnItemCompletedChanged(id, isChecked))
                }

                override fun onItemDeleteClick(id: Long) {
                    dispatch(ListUiEvent.OnDeleteItem(id))
                }
            }
        )

    private val todoEditText = activity.findViewById<EditText>(R.id.todo_edit)

    init {
        activity.findViewById<RecyclerView>(R.id.recycler_view).apply {
            adapter = this@ListView.adapter
        }

        activity.findViewById<View>(R.id.add_button).setOnClickListener {
            dispatch(ListUiEvent.OnAddItem(todoEditText.text.toString()))
            todoEditText.text = null
        }
    }

    override fun subscribe(models: Observable<ListViewModel>): Disposable =
        CompositeDisposable().apply {
            this += models
                .map(ListViewModel::items)
                .distinctUntilChanged { a, b -> a === b }
                .subscribe { adapter.items = it }

            this += models
                .mapNotNull { it.detailsRedirectItemId }
                .subscribe {
                    dispatch(ListUiEvent.OnRedirectedToItemDetails)
                    activity.startActivity(DetailsActivity.createIntent(activity, it))
                }
        }
}
