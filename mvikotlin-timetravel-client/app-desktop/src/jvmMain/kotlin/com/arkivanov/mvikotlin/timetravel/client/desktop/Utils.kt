package com.arkivanov.mvikotlin.timetravel.client.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorObservable
import java.awt.Dimension
import java.awt.FileDialog
import java.awt.Toolkit
import java.io.File
import javax.swing.SwingUtilities

fun <T> invokeOnAwtSync(block: () -> T): T {
    var result: T? = null
    SwingUtilities.invokeAndWait { result = block() }

    @Suppress("UNCHECKED_CAST")
    return result as T
}

fun getPreferredWindowSize(desiredWidth: Int, desiredHeight: Int): IntSize {
    val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
    val preferredWidth: Int = (screenSize.width * 0.8f).toInt()
    val preferredHeight: Int = (screenSize.height * 0.8f).toInt()
    val width: Int = if (desiredWidth < preferredWidth) desiredWidth else preferredWidth
    val height: Int = if (desiredHeight < preferredHeight) desiredHeight else preferredHeight

    return IntSize(width, height)
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
