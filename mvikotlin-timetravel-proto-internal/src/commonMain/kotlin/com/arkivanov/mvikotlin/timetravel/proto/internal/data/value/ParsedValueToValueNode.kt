package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

fun ParsedValue.toTree(name: String? = null): ValueNode =
    when (this) {
        is ParsedValue.Primitive -> toTree(name = name)
        is ParsedValue.Object -> toTree(name = name)
    }

private fun ParsedValue.Primitive.toTree(name: String? = null): ValueNode =
    when (this) {
        is ParsedValue.Primitive.Int -> ValueNode(name = name, type = "Int", value = value.toString())
        is ParsedValue.Primitive.Long -> ValueNode(name = name, type = "Long", value = value.toString())
        is ParsedValue.Primitive.Short -> ValueNode(name = name, type = "Short", value = value.toString())
        is ParsedValue.Primitive.Byte -> ValueNode(name = name, type = "Byte", value = value.toString())
        is ParsedValue.Primitive.Float -> ValueNode(name = name, type = "Float", value = value.toString())
        is ParsedValue.Primitive.Double -> ValueNode(name = name, type = "Double", value = value.toString())
        is ParsedValue.Primitive.Char -> ValueNode(name = name, type = "Char", value = value.toString())
        is ParsedValue.Primitive.Boolean -> ValueNode(name = name, type = "Boolean", value = value.toString())
    }

private fun ParsedValue.Object.toTree(name: String? = null): ValueNode =
    when (this) {
        is ParsedValue.Object.Int -> ValueNode(name = name, type = "Int", value = "{Int} $value")
        is ParsedValue.Object.Long -> ValueNode(name = name, type = "Long", value = "{Long} $value")
        is ParsedValue.Object.Short -> ValueNode(name = name, type = "Short", value = "{Short} $value")
        is ParsedValue.Object.Byte -> ValueNode(name = name, type = "Byte", value = "{Byte} $value")
        is ParsedValue.Object.Float -> ValueNode(name = name, type = "Float", value = "{Float} $value")
        is ParsedValue.Object.Double -> ValueNode(name = name, type = "Double", value = "{Double} $value")
        is ParsedValue.Object.Char -> ValueNode(name = name, type = "Char", value = "{Char} $value")
        is ParsedValue.Object.Boolean -> ValueNode(name = name, type = "Boolean", value = "{Boolean} $value")
        is ParsedValue.Object.String -> ValueNode(name = name, type = "String", value = "\"$value\"")
        is ParsedValue.Object.IntArray -> toTree(name)
        is ParsedValue.Object.LongArray -> toTree(name)
        is ParsedValue.Object.ShortArray -> toTree(name)
        is ParsedValue.Object.ByteArray -> toTree(name)
        is ParsedValue.Object.FloatArray -> toTree(name)
        is ParsedValue.Object.DoubleArray -> toTree(name)
        is ParsedValue.Object.CharArray -> toTree(name)
        is ParsedValue.Object.BooleanArray -> toTree(name)
        is ParsedValue.Object.Array -> toTree(name)
        is ParsedValue.Object.Iterable -> toTree(name)
        is ParsedValue.Object.Map -> toTree(name)
        is ParsedValue.Object.Other -> toTree(name)
        is ParsedValue.Object.Unparsed -> ValueNode(name = name, type = type, value = value)
    }

private fun ParsedValue.Object.IntArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "IntArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.LongArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "LongArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.ShortArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "ShortArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.ByteArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "ByteArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.FloatArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "FloatArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.DoubleArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "DoubleArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.CharArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "CharArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.BooleanArray.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = "BooleanArray",
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
    )

private fun ParsedValue.Object.Array.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = type,
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> item.toTree(name = "[$index]") } }
    )

private fun ParsedValue.Object.Iterable.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = type,
        value = value,
        size = { it.size },
        children = { mapIndexed { index, item -> item.toTree(name = "[$index]") } }
    )

private fun ParsedValue.Object.Map.toTree(name: String?): ValueNode =
    iterableValueNode(
        name = name,
        type = type,
        value = value,
        size = { it.size },
        children = {
            map { (key, value) ->
                val keyNode = key.toTree(name = "key")
                val valueNode = value.toTree(name = "value")

                ValueNode(
                    type = "${keyNode.value} -> ${valueNode.value}",
                    children = listOf(keyNode, valueNode)
                )
            }
        }
    )

private fun ParsedValue.Object.Other.toTree(name: String?): ValueNode =
    ValueNode(
        name = name,
        type = type,
        value = value?.let { "{$type}" } ?: "null",
        children = value?.map { (name, obj) -> obj.toTree(name = name) } ?: emptyList()
    )

private inline fun <T : Any> iterableValueNode(
    name: String?,
    type: String,
    value: T?,
    size: (T) -> Int,
    children: T.() -> List<ValueNode>
): ValueNode =
    ValueNode(
        name = name,
        type = type,
        value = if (value == null) "null" else "{$type[${size(value)}]}",
        children = value?.let(children) ?: emptyList()
    )
