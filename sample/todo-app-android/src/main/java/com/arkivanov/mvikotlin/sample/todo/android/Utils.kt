package com.arkivanov.mvikotlin.sample.todo.android

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.timetravel.store.TimeTravelStoreFactory

val storeFactoryInstance =
    if (BuildConfig.DEBUG) {
        LoggingStoreFactory(delegate = TimeTravelStoreFactory(fallback = DefaultStoreFactory))
    } else {
        DefaultStoreFactory
    }

val Context.app: App get() = applicationContext as App

fun <T : Any> T?.requireNotNull(): T = requireNotNull(this)

fun <T : View> View.getViewById(@IdRes id: Int): T = findViewById<T>(id).requireNotNull()

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

open class SimpleTextWatcher : TextWatcher {
    override fun afterTextChanged(s: Editable) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
}

inline fun Lifecycle.doOnDestroy(crossinline block: () -> Unit) {
    addObserver(
        object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                block()
            }
        }
    )
}
