package com.arkivanov.mvikotlin.core.utils.statekeeper

import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import kotlin.reflect.KClass

internal actual val KClass<*>.key: String get() = qualifiedName ?: toString()
