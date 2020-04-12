package com.arkivanov.mvikotlin.sample.todo.android.list

import android.view.View
import android.widget.EditText
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.android.R
import com.arkivanov.mvikotlin.sample.todo.android.SimpleTextWatcher
import com.arkivanov.mvikotlin.sample.todo.android.getViewById
import com.arkivanov.mvikotlin.sample.todo.android.setTextCompat
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView.Model

class TodoAddViewImpl(root: View) : BaseMviView<Model, Event>(), TodoAddView {

    private val editText = root.getViewById<EditText>(R.id.todo_edit)

    private val textWatcher =
        object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dispatch(Event.TextChanged(s.toString()))
            }
        }

    override val renderer =
        diff<Model> {
            diff(Model::text) {
                editText.setTextCompat(it, textWatcher)
            }
        }

    init {
        root.getViewById<View>(R.id.add_button).setOnClickListener {
            dispatch(Event.AddClicked)
        }

        editText.addTextChangedListener(textWatcher)
    }
}
