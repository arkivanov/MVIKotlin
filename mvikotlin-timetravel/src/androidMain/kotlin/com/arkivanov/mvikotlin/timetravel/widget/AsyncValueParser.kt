package com.arkivanov.mvikotlin.timetravel.widget

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueParser
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

internal object AsyncValueParser {

    private val executors = Executors.newSingleThreadExecutor()

    fun parse(value: Any, callback: (String) -> Unit) {
        executors.submit(Task(value, WeakReference(callback)))
    }

    private class Task(
        private val value: Any,
        private val callback: WeakReference<(String) -> Unit>
    ) : Runnable {
        override fun run() {
            val value = ValueParser().parseValue(obj = value)
            val text = value.toFormattedString()
            callback.get()?.invoke(text)
        }

        private fun ValueNode.toFormattedString(): String =
            buildString {
                appendNode(node = this@toFormattedString, indent = 0)
            }

        private fun StringBuilder.appendNode(node: ValueNode, indent: Int) {
            append(" ".repeat(indent))
            appendLine(node.title)

            node.children.forEach {
                appendNode(node = it, indent = indent + 2)
            }
        }
    }
}
