package com.arkivanov.rxkotlin.sample.todo.android

import android.content.Context
import android.text.TextWatcher
import android.widget.EditText
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory

val storeFactory =
    if (BuildConfig.DEBUG) {
        LoggingStoreFactory(delegate = TimeTravelStoreFactory)
    } else {
        DefaultStoreFactory
    }

val Context.app: App get() = applicationContext as App

fun EditText.setTextCompat(text: CharSequence, textWatcher: TextWatcher? = null) {
    val savedSelectionStart = selectionStart
    val savedSelectionEnd = selectionEnd
    textWatcher?.also(::removeTextChangedListener)
    setText(text)
    textWatcher?.also(::addTextChangedListener)
    if (savedSelectionEnd <= text.length) {
        setSelection(savedSelectionStart, savedSelectionEnd)
    } else {
        setSelection(text.length)
    }
}
