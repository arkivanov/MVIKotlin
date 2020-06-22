package com.arkivanov.mvikotlin.timetravel.widget

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueTextTreeBuilder
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.parseObject
import java.lang.ref.WeakReference
import java.util.concurrent.Executors

internal object AsyncValueParser {

    private val executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors().coerceAtLeast(2))
    private val treeBuilder = ValueTextTreeBuilder(newNode = ::Node, addChild = { children.add(it) })

    fun parse(value: Any, callback: (String) -> Unit) {
        executors.submit(Task(value, WeakReference(callback)))
    }

    private class Task(
        private val value: Any,
        private val callback: WeakReference<(String) -> Unit>
    ) : Runnable {
        override fun run() {
            val value = parseObject(obj = value)
            val tree = treeBuilder.build(value)
            val text = StringBuilder().appendNode(tree).toString()
            callback.get()?.invoke(text)
        }

        private fun StringBuilder.appendNode(node: Node, indent: Int = 0): StringBuilder {
            repeat(indent) {
                append(' ')
            }

            appendln(node.text)

            node.children.forEach {
                appendNode(node = it, indent = indent + 2)
            }

            return this
        }
    }

    private class Node(val text: String) {
        val children: MutableList<Node> = ArrayList()
    }
}
