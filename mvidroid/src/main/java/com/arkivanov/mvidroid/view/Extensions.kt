package com.arkivanov.mvidroid.view

import android.support.annotation.MainThread

/**
 * Registers a diff strategy that compares values by equals. See [MviBaseView.registerDiff] for more information.
 */
@MainThread
fun <ViewModel : Any, T> MviBaseView<ViewModel, *>.registerDiffByEquals(mapper: ViewModel.() -> T, consumer: (T) -> Unit) {
    registerDiff(mapper, { newValue, oldValue -> newValue == oldValue }, consumer)
}

/**
 * Registers a diff strategy that compares values by reference. See [MviBaseView.registerDiff] for more information.
 */
@MainThread
fun <ViewModel : Any, T> MviBaseView<ViewModel, *>.registerDiffByReference(mapper: ViewModel.() -> T, consumer: (T) -> Unit) {
    registerDiff(mapper, { newValue, oldValue -> newValue === oldValue }, consumer)
}
