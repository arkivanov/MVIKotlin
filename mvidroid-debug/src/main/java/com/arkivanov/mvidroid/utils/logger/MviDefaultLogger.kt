package com.arkivanov.mvidroid.utils.logger

import android.util.Log

object MviDefaultLogger : MviLogger {

    private const val TAG = "MVIDroid"

    override fun log(text: String) {
        Log.v(TAG, text)
    }
}