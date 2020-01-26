package com.arkivanov.rxkotlin.sample.android.app.list

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.main.view.AbstractView
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView.Event
import com.arkivanov.mvikotlin.sample.shared.view.TodoAddView.Model
import com.arkivanov.rxkotlin.sample.android.app.R
import com.arkivanov.rxkotlin.sample.android.app.setTextCompat

class TodoAddViewImpl(root: View) : AbstractView<Model, Event>(), TodoAddView {

    private val editText = root.findViewById<EditText>(R.id.todo_edit)

    private val textWatcher =
        object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dispatch(Event.TextChanged(s?.toString() ?: ""))
            }
        }

    private val renderer =
        diff<Model> {
            diff(get = Model::text) {
                editText.setTextCompat(it, textWatcher)
            }
        }

    init {
        root.findViewById<View>(R.id.add_button).setOnClickListener {
            dispatch(Event.AddClicked)
        }

        editText.addTextChangedListener(textWatcher)
    }

    override fun render(model: Model) {
        renderer.render(model)
    }
}
