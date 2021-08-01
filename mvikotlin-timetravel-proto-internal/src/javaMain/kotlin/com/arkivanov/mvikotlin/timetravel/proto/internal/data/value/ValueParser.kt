package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.TypeVariable

@Suppress("EmptyDefaultConstructor")
actual class ValueParser actual constructor() {

    private val visitedObjects = HashSet<Any>()

    actual fun parseValue(obj: Any): ValueNode {
        visitedObjects.clear()

        return value(value = obj)
    }

    actual fun parseType(obj: Any): String =
        getTypeName(value = obj)

    private fun value(value: Any? = null, clazz: Class<*>? = null, name: String? = null): ValueNode =
        when {
            value != null -> valueOfObject(value = value, name = name)
            clazz != null -> valueOfNull(clazz = clazz, name = name)
            else -> ValueNode(name = name, type = getTypeName(), value = "null")
        }

    private fun valueOfObject(value: Any, name: String? = null): ValueNode {
        if (value in visitedObjects) {
            return ValueNode(name = name, type = getTypeName(value = value), value = "Recursive reference")
        }

        visitedObjects += value
        try {
            return when (value) {
                is Int -> ValueNode(name = name, type = "Int", value = "{Int} $value")
                is Long -> ValueNode(name = name, type = "Long", value = "{Long} $value")
                is Short -> ValueNode(name = name, type = "Short", value = "{Short} $value")
                is Byte -> ValueNode(name = name, type = "Byte", value = "{Byte} $value")
                is Float -> ValueNode(name = name, type = "Float", value = "{Float} $value")
                is Double -> ValueNode(name = name, type = "Double", value = "{Double} $value")
                is Char -> ValueNode(name = name, type = "Char", value = "{Char} $value")
                is Boolean -> ValueNode(name = name, type = "Boolean", value = "{Boolean} $value")
                is String -> ValueNode(name = name, type = "String", value = "\"$value\"")
                is IntArray -> value.toTree(name = name)
                is LongArray -> value.toTree(name = name)
                is ShortArray -> value.toTree(name = name)
                is ByteArray -> value.toTree(name = name)
                is FloatArray -> value.toTree(name = name)
                is DoubleArray -> value.toTree(name = name)
                is CharArray -> value.toTree(name = name)
                is BooleanArray -> value.toTree(name = name)
                is Array<*> -> value.toTree(name = name)
                is Iterable<*> -> value.toTree(name = name)
                is Map<*, *> -> value.toTree(name = name)
                else -> value.toTreeOther(name = name)
            }
        } finally {
            visitedObjects -= value
        }
    }

    private fun valueOfNull(clazz: Class<*>, name: String? = null): ValueNode =
        when {
            clazz == Int::class.java -> ValueNode(name = name, type = "Int", value = "{Int} null")
            clazz == Long::class.java -> ValueNode(name = name, type = "Long", value = "{Long} null")
            clazz == Short::class.java -> ValueNode(name = name, type = "Short", value = "{Short} null")
            clazz == Byte::class.java -> ValueNode(name = name, type = "Byte", value = "{Byte} null")
            clazz == Float::class.java -> ValueNode(name = name, type = "Float", value = "{Float} null")
            clazz == Double::class.java -> ValueNode(name = name, type = "Double", value = "{Double} null")
            clazz == Char::class.java -> ValueNode(name = name, type = "Char", value = "{Char} null")
            clazz == Boolean::class.java -> ValueNode(name = name, type = "Boolean", value = "{Boolean} null")
            clazz == String::class.java -> ValueNode(name = name, type = "String", value = "null")
            clazz == IntArray::class.java -> ValueNode(name = name, type = "IntArray", value = "null")
            clazz == LongArray::class.java -> ValueNode(name = name, type = "LongArray", value = "null")
            clazz == ShortArray::class.java -> ValueNode(name = name, type = "ShortArray", value = "null")
            clazz == ByteArray::class.java -> ValueNode(name = name, type = "ByteArray", value = "null")
            clazz == FloatArray::class.java -> ValueNode(name = name, type = "FloatArray", value = "null")
            clazz == DoubleArray::class.java -> ValueNode(name = name, type = "DoubleArray", value = "null")
            clazz == CharArray::class.java -> ValueNode(name = name, type = "CharArray", value = "null")
            clazz == BooleanArray::class.java -> ValueNode(name = name, type = "BooleanArray", value = "null")
            clazz == Array<Any?>::class.java -> ValueNode(name = name, type = getTypeName(clazz = Array<Any?>::class.java), value = "null")
            Iterable::class.java.isAssignableFrom(clazz) -> ValueNode(name = name, type = getTypeName(clazz = clazz), value = "null")
            Map::class.java.isAssignableFrom(clazz) -> ValueNode(name = name, type = getTypeName(clazz = clazz), value = "null")
            else -> ValueNode(name = name, type = getTypeName(clazz = clazz), value = "null")
        }

    private fun IntArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "IntArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun LongArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "LongArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun ShortArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "ShortArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun ByteArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "ByteArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun FloatArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "FloatArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun DoubleArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "DoubleArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun CharArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "CharArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun BooleanArray.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = "BooleanArray",
            value = this,
            children = { mapIndexed { index, item -> ValueNode(type = "[$index] = $item") } }
        )

    private fun Iterable<*>.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = getTypeName(value = this),
            value = this,
            children = { mapIndexed { index, item -> value(value = item, name = "[$index]") } }
        )

    private fun Map<*, *>.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = getTypeName(value = this),
            value = this,
            children = {
                map { (key, value) ->
                    val keyNode = value(value = key, name = "key")
                    val valueNode = value(value = value, name = "value")

                    ValueNode(
                        type = "${keyNode.value} -> ${valueNode.value}",
                        children = listOf(keyNode, valueNode)
                    )
                }
            }
        )

    private fun Array<*>.toTree(name: String? = null): ValueNode =
        iterableValueNode(
            name = name,
            type = getTypeName(value = this),
            value = this,
            children = { mapIndexed { index, item -> value(value = item, name = "[$index]") } }
        )

    private fun Any.toTreeOther(name: String? = null): ValueNode =
        when (this) {
            is Throwable -> toTree(name = name)
            else -> toTreeDefault(name = name)
        }

    private fun Throwable.toTree(name: String? = null): ValueNode {
        val type = getTypeName(value = this)

        return ValueNode(
            name = name,
            type = type,
            value = "{$type}",
            children = listOfNotNull(
                ValueNode(name = "message", type = "String", value = message),
                cause?.toTree(name = "cause")
            )
        )
    }

    private fun Any.toTreeDefault(name: String? = null): ValueNode {
        val children = ArrayList<ValueNode>()

        accessEachField {
            children += it.getValue(obj = this, name = it.name)
        }

        val type = getTypeName(value = this)

        return ValueNode(
            name = name,
            type = type,
            value = "{$type}",
            children = children
        )
    }

    private inline fun <T : Any> iterableValueNode(
        name: String?,
        type: String,
        value: T?,
        children: T.() -> List<ValueNode>
    ): ValueNode {
        val childItems = value?.let(children) ?: emptyList()

        return ValueNode(
            name = name,
            type = type,
            value = if (value == null) "null" else "{$type[${childItems.size}]}",
            children = childItems
        )
    }

    private fun Field.getValue(obj: Any, name: String? = null): ValueNode =
        when (type) {
            Int::class.javaPrimitiveType -> ValueNode(name = name, type = "Int", value = getInt(obj).toString())
            Long::class.javaPrimitiveType -> ValueNode(name = name, type = "Long", value = getLong(obj).toString())
            Short::class.javaPrimitiveType -> ValueNode(name = name, type = "Short", value = getShort(obj).toString())
            Byte::class.javaPrimitiveType -> ValueNode(name = name, type = "Byte", value = getByte(obj).toString())
            Float::class.javaPrimitiveType -> ValueNode(name = name, type = "Float", value = getFloat(obj).toString())
            Double::class.javaPrimitiveType -> ValueNode(name = name, type = "Double", value = getDouble(obj).toString())
            Char::class.javaPrimitiveType -> ValueNode(name = name, type = "Char", value = getChar(obj).toString())
            Boolean::class.javaPrimitiveType -> ValueNode(name = name, type = "Boolean", value = getBoolean(obj).toString())
            else -> value(value = get(obj), clazz = type, name = name)
        }

    private companion object {
        private val BLACK_LIST_FIELDS = hashSetOf("serialVersionUID", "INSTANCE")

        private inline fun Field.access(block: () -> Unit) {
            @Suppress("DEPRECATION") // Required for Java 8
            val isFieldAccessible = isAccessible
            if (isFieldAccessible || trySetAccessibleCompat()) {
                try {
                    block()
                } finally {
                    if (!isFieldAccessible) {
                        isAccessible = false
                    }
                }
            }
        }

        private fun Field.trySetAccessibleCompat(): Boolean =
            try {
                isAccessible = true
                true
            } catch (e: SecurityException) {
                false
            }

        private fun Field.isValidForParsing(): Boolean =
            !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)

        private fun Any.accessEachField(block: (Field) -> Unit) {
            javaClass.allFields.forEach { field ->
                if (field.isValidForParsing()) {
                    field.access {
                        val fieldName = field.name
                        if (fieldName.isAllowedFieldName()) {
                            block(field)
                        }
                    }
                }
            }
        }

        private val Class<*>.allFields: List<Field>
            get() {
                val list = ArrayList<Field>()
                var cls: Class<*>? = this
                while ((cls != null) && (cls != Object::class.java)) {
                    list += cls.declaredFields
                    cls = cls.superclass
                }

                return list
            }

        private fun String.isAllowedFieldName(): Boolean = !startsWith("$") && !BLACK_LIST_FIELDS.contains(this)

        fun getTypeName(value: Any? = null, clazz: Class<*>? = null): String {
            val valueClass = value?.javaClass ?: clazz ?: return "Object"

            if (valueClass.isArray) {
                return "Array<${valueClass.componentType?.getFixedName() ?: "T"}>"
            }

            return valueClass
                .typeParameters
                .takeUnless(Array<*>::isEmpty)
                ?.joinToString(
                    separator = ", ",
                    prefix = "${valueClass.getFixedName()}<",
                    postfix = ">",
                    transform = TypeVariable<*>::getName
                )
                ?: valueClass.getFixedName()
        }

        private fun Class<*>.getFixedName(): String =
            this.kotlin.simpleName ?: simpleName
    }
}
