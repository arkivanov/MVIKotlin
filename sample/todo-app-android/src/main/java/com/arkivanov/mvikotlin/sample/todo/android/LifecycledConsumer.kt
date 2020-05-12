package com.arkivanov.mvikotlin.sample.todo.android

import androidx.lifecycle.LifecycleOwner

interface LifecycledConsumer<in T> : LifecycleOwner {

    val input: (T) -> Unit
}
