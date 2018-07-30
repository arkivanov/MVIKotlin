package com.arkivanov.mvidroid.sample.ui.details

import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import com.arkivanov.mvidroid.sample.R
import com.arkivanov.mvidroid.sample.component.details.DetailsUiEvent
import com.arkivanov.mvidroid.sample.ui.plusAssign
import com.arkivanov.mvidroid.sample.utils.SimpleTextWatcher
import com.arkivanov.mvidroid.view.MviAbstractView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class DetailsView(
    private val activity: AppCompatActivity
) : MviAbstractView<DetailsViewModel, DetailsUiEvent>() {

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

    fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_delete -> {
                dispatch(DetailsUiEvent.OnDelete)
                true
            }

            else -> false
        }

    override fun subscribe(models: Observable<DetailsViewModel>): Disposable =
        CompositeDisposable().apply {
            this += models
                .map(DetailsViewModel::text)
                .distinctUntilChanged()
                .filter { !TextUtils.equals(editText.text, it) }
                .subscribe {
                    editText.removeTextChangedListener(textChangedListener)
                    editText.setText(it)
                    editText.addTextChangedListener(textChangedListener)
                    editText.setSelection(it.length)
                }

            this += models
                .map(DetailsViewModel::isCompleted)
                .distinctUntilChanged()
                .filter { checkBox.isChecked != it }
                .subscribe {
                    checkBox.setOnCheckedChangeListener(null)
                    checkBox.isChecked = it
                    checkBox.setOnCheckedChangeListener(onCheckedChangeListener)
                }

            this += models
                .map(DetailsViewModel::isFinished)
                .filter { it }
                .distinctUntilChanged()
                .subscribe { activity.finish() }
        }
}
