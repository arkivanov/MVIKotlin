package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

data class ValueNode(
    val name: String? = null,
    val type: String,
    val value: String? = null,
    val children: List<ValueNode> = emptyList()
) {

    val title: String =
        buildString {
            if (name != null) {
                append(name)
                append(": ")
            }

            append(type)

            if (value != null) {
                append(" = ")
                append(value)
            }
        }
}
