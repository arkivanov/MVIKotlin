package com.arkivanov.mvikotlin.core.debug.widget

import android.view.View

internal fun View.setOnClickListener(listener: (() -> Unit)?) {
    setOnClickListener(
        listener?.let {
            View.OnClickListener { listener() }
        }
    )
}
