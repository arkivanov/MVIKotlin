package com.arkivanov.mvidroid.sample.ui.details

import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import com.arkivanov.mvidroid.sample.R
import com.arkivanov.mvidroid.sample.component.details.DetailsUiEvent
import com.arkivanov.mvidroid.sample.utils.SimpleTextWatcher
import com.arkivanov.mvidroid.view.MviBaseView

class DetailsView(activity: AppCompatActivity) : MviBaseView<DetailsViewModel, DetailsUiEvent>() {

    private val textChangedListener =
        object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable) {
                dispatch(DetailsUiEvent.OnTextChanged(s.toString()))
            }
        }

    private val editText = activity.findViewById<EditText>(R.id.edit).apply {
        addTextChangedListener(textChangedListener)
    }

    private val onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            dispatch(DetailsUiEvent.OnCompletedChanged(isChecked))
        }

    private val checkBox = activity.findViewById<CheckBox>(R.id.check_box).apply {
        setOnCheckedChangeListener(onCheckedChangeListener)
    }

    init {
        registerDiffByEquals(editText, DetailsViewModel::text) {
            if (it != text) {
                removeTextChangedListener(textChangedListener)
                setText(it)
                addTextChangedListener(textChangedListener)
                setSelection(it.length)
            }
        }

        registerDiffByEquals(checkBox, DetailsViewModel::isCompleted) {
            if (it != isChecked) {
                setOnCheckedChangeListener(null)
                isChecked = it
                setOnCheckedChangeListener(onCheckedChangeListener)
            }
        }

        registerDiffByEquals(activity, DetailsViewModel::isFinished) {
            if (it) {
                finish()
            }
        }
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_delete -> {
                dispatch(DetailsUiEvent.OnDelete)
                true
            }

            else -> false
        }
}
