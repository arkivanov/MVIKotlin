package com.arkivanov.mvikotlin.core.utils

import android.os.Looper

internal actual val isMainThread: Boolean get() = Thread.currentThread().id == Looper.getMainLooper().thread.id

internal actual val currentThreadDescription: String get() = Thread.currentThread().name

