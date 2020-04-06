package com.arkivanov.mvikotlin.core.binder

/**
 * Defines how a [Binder] is attached to a [Lifecycle][com.arkivanov.mvikotlin.core.lifecycle.Lifecycle]
 */
enum class BinderLifecycleMode {

    CREATE_DESTROY, START_STOP, RESUME_PAUSE
}
