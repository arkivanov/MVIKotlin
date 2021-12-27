package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorObservable
import java.awt.FileDialog
import java.io.File
import javax.swing.SwingUtilities

fun <T> invokeOnAwtSync(block: () -> T): T {
    var result: T? = null
    SwingUtilities.invokeAndWait { result = block() }

    @Suppress("UNCHECKED_CAST")
    return result as T
}

@Composable
fun <T> BehaviorObservable<T>.subscribeAsState(): State<T> {
    val state = remember(this) { mutableStateOf(value) }

    DisposableEffect(this) {
        val disposable = subscribe { state.value = it }
        onDispose(disposable::dispose)
    }

    return state
}

val FileDialog.selectedFile: File?
    get() = if ((directory != null) && (file != null)) File(directory, file) else null
