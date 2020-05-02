package com.arkivanov.mvikotlin.utils.internal

class ValueTextTreeBuilder<T>(
    private val newNode: (text: String) -> T,
    private val addChild: T.(child: T) -> Unit
) {
    fun build(value: Value): T {
        val root = newNode(value.getNodeText())
        root.addChildren(value)

        return root
    }

    private fun Value.toNode(prefix: String? = null): T {
        val node = newNode(getNodeText(prefix = prefix))
        node.addChildren(this)

        return node
    }

    private fun Value.getNodeText(prefix: String? = null): String {
        val builder = StringBuilder()
        if (prefix != null) {
            builder.append(prefix)
            builder.append(": ")
        }

        builder.append(type)

        val valueDescription: String? = valueDescription
        if (valueDescription != null) {
            builder.append(" = ")
            builder.append(valueDescription)
        }

        return builder.toString()
    }

    private fun T.addChildren(value: Value) {
        when (value) {
            is Value.Primitive.Int,
            is Value.Primitive.Long,
            is Value.Primitive.Short,
            is Value.Primitive.Byte,
            is Value.Primitive.Float,
            is Value.Primitive.Double,
            is Value.Primitive.Char,
            is Value.Primitive.Boolean,
            is Value.Object.String,
            is Value.Object.Unparsed -> Unit
            is Value.Object.IntArray -> addChildren(value)
            is Value.Object.LongArray -> addChildren(value)
            is Value.Object.ShortArray -> addChildren(value)
            is Value.Object.ByteArray -> addChildren(value)
            is Value.Object.FloatArray -> addChildren(value)
            is Value.Object.DoubleArray -> addChildren(value)
            is Value.Object.CharArray -> addChildren(value)
            is Value.Object.BooleanArray -> addChildren(value)
            is Value.Object.Array -> addChildren(value)
            is Value.Object.Iterable -> addChildren(value)
            is Value.Object.Map -> addChildren(value)
            is Value.Object.Other -> addChildren(value)
        }.let {}
    }

    private fun T.addChildren(array: Value.Object.IntArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.LongArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.ShortArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.ByteArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.FloatArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.DoubleArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.CharArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addChildren(array: Value.Object.BooleanArray) {
        array.value?.forEachIndexed { index, value -> addArrayItem(index, value.toString()) }
    }

    private fun T.addArrayItem(index: Int, value: String) {
        addChild(newNode("[$index]: $value"))
    }

    private fun T.addChildren(array: Value.Object.Array) {
        array.value?.forEachIndexed { index, value ->
            addChild(value.toNode(prefix = "[$index]"))
        }
    }

    private fun T.addChildren(iterable: Value.Object.Iterable) {
        iterable.value?.forEachIndexed { index, value ->
            addChild(value.toNode(prefix = "[$index]"))
        }
    }

    private fun T.addChildren(map: Value.Object.Map) {
        map.value?.forEach { (key, value) ->
            addChild(value.toNode(prefix = "[${key.valueDescription}]"))
        }
    }

    private fun T.addChildren(other: Value.Object.Other) {
        other.value?.forEach { (key, value) ->
            addChild(value.toNode(prefix = key))
        }
    }

    private val Value.valueDescription: String?
        get() =
            when (this) {
                is Value.Primitive.Int -> value.toString()
                is Value.Primitive.Long -> value.toString()
                is Value.Primitive.Short -> value.toString()
                is Value.Primitive.Byte -> value.toString()
                is Value.Primitive.Float -> value.toString()
                is Value.Primitive.Double -> value.toString()
                is Value.Primitive.Char -> value.toString()
                is Value.Primitive.Boolean -> value.toString()
                is Value.Object.String -> value?.let { "\"$it\"" } ?: "null"
                is Value.Object.IntArray -> if (value == null) "null" else null
                is Value.Object.LongArray -> if (value == null) "null" else null
                is Value.Object.ShortArray -> if (value == null) "null" else null
                is Value.Object.ByteArray -> if (value == null) "null" else null
                is Value.Object.FloatArray -> if (value == null) "null" else null
                is Value.Object.DoubleArray -> if (value == null) "null" else null
                is Value.Object.CharArray -> if (value == null) "null" else null
                is Value.Object.BooleanArray -> if (value == null) "null" else null
                is Value.Object.Array -> if (value == null) "null" else null
                is Value.Object.Iterable -> if (value == null) "null" else null
                is Value.Object.Map -> if (value == null) "null" else null
                is Value.Object.Other -> if (value == null) "null" else null
                is Value.Object.Unparsed -> value
            }
}
