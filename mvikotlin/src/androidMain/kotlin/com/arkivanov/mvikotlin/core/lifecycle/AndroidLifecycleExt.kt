package com.arkivanov.mvikotlin.core.lifecycle

import androidx.lifecycle.Lifecycle as AndroidLifecycle

/**
 * Converts Androidx [Lifecycle][AndroidLifecycle] to MviKotlin [Lifecycle].
 * Requires [Java 1.8 source and target compatibility](https://developer.android.com/studio/write/java8-support).
 */
fun AndroidLifecycle.asMviLifecycle(): Lifecycle = AndroidLifecycleAdapter(this)
