package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

fun ValueNode.toFormattedString(): String =
    buildString {
        appendNode(node = this@toFormattedString, indent = 0)
    }

private fun StringBuilder.appendNode(node: ValueNode, indent: Int) {
    append(" ".repeat(indent))
    appendLine(node.value.toString())

    node.children.forEach {
        appendNode(node = it, indent = indent + 2)
    }
}
