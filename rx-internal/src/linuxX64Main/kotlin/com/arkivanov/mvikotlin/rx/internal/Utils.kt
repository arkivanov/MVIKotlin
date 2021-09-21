package com.arkivanov.mvikotlin.rx.internal

internal actual val PTHREAD_MUTEX_RECURSIVE: Int get() = platform.posix.PTHREAD_MUTEX_RECURSIVE.toInt()
