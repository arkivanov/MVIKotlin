package com.arkivanov.mvikotlin.sample.coroutines.app.details

import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.arkivanov.mvikotlin.sample.coroutines.app.R
import com.arkivanov.mvikotlin.sample.coroutines.app.SimpleTextWatcher
import com.arkivanov.mvikotlin.sample.coroutines.app.getViewById
import com.arkivanov.mvikotlin.sample.coroutines.app.setTextCompat
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Model

class DetailsViewImpl(root: View) : BaseMviView<Model, Event>(), DetailsView {

    private val textWatcher =
        object : SimpleTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                dispatch(Event.TextChanged(text = s.toString()))
            }
        }

    private val editText = root.getViewById<EditText>(R.id.edit_text)
    private val checkBox = root.getViewById<CheckBox>(R.id.check_completed)

    override val renderer: ViewRenderer<Model> =
        diff {
            diff(Model::text) { editText.setTextCompat(it, textWatcher) }
            diff(get = Model::isDone, set = checkBox::setChecked)
        }

    init {
        root.getViewById<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.details)
            setOnMenuItemClickListener(::onMenuItemClick)
        }

        editText.addTextChangedListener(textWatcher)
        checkBox.setOnClickListener { dispatch(Event.DoneClicked) }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_delete -> {
                dispatch(Event.DeleteClicked)
                true
            }

            else -> false
        }
}
