package com.arkivanov.mvidroid.sample.details.ui.details

import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import com.arkivanov.mvidroid.sample.common.utils.SimpleTextWatcher
import com.arkivanov.mvidroid.sample.details.R
import com.arkivanov.mvidroid.sample.details.component.DetailsEvent
import com.arkivanov.mvidroid.view.MviBaseView

internal class DetailsView(root: View) : MviBaseView<DetailsViewModel, DetailsEvent>() {

    private val textChangedListener =
        object : SimpleTextWatcher {
            override fun afterTextChanged(s: Editable) {
                dispatch(DetailsEvent.OnTextChanged(s.toString()))
            }
        }

    private val editText = root.findViewById<EditText>(R.id.edit_text).apply {
        addTextChangedListener(textChangedListener)
    }

    private val onCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            dispatch(DetailsEvent.OnSetCompleted(isChecked))
        }

    private val checkBox = root.findViewById<CheckBox>(R.id.check_completed).apply {
        setOnCheckedChangeListener(onCheckedChangeListener)
    }

    private val errorView = root.findViewById<View>(R.id.text_error)

    init {
        root.findViewById<Toolbar>(R.id.toolbar).apply {
            inflateMenu(R.menu.details)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_delete -> {
                        dispatch(DetailsEvent.OnDelete);
                        true
                    }

                    else -> false
                }
            }
        }

        registerDiffByEquals(editText, DetailsViewModel::text) {
            if (!TextUtils.equals(it, text)) {
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

        registerDiffByEquals(this, DetailsViewModel::isError) {
            editText.setVisible(!it)
            checkBox.setVisible(!it)
            errorView.setVisible(it)
        }
    }

    private companion object {
        private fun View.setVisible(isVisible: Boolean) {
            visibility = if (isVisible) View.VISIBLE else View.GONE
        }
    }
}