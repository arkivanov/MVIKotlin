package com.arkivanov.mvikotlin.timetravel.chrome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.subject.behavior.BehaviorObservable
import org.jetbrains.compose.web.attributes.AttrsBuilder
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.w3c.dom.Element

internal fun AttrsScope<*>.classesOfNotNull(vararg classes: String?) {
    classes(*classes.filterNotNull().toTypedArray())
}

@Composable
internal fun <T> BehaviorObservable<T>.subscribeAsState(): State<T> {
    val state = remember(this) { mutableStateOf(value) }

    DisposableEffect(this) {
        val disposable = subscribe { state.value = it }
        onDispose(disposable::dispose)
    }

    return state
}

internal fun <T : Element> AttrsBuilder<T>.add(attrs: AttrBuilderContext<T>?) {
    attrs?.invoke(this)
}

internal fun <T> jsObject(builder: T.() -> Unit): T =
    js("{}").unsafeCast<T>().apply(builder)
