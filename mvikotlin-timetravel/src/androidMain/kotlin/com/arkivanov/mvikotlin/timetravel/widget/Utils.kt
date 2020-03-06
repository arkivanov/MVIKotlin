package com.arkivanov.mvikotlin.timetravel.widget

import android.view.View

internal fun View.setOnClickListener(listener: (() -> Unit)?) {
    setOnClickListener(
        listener?.let {
            View.OnClickListener { listener() }
        }
    )
}
