package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.Value
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.type
import java.lang.reflect.Field
import java.lang.reflect.TypeVariable

private val BLACK_LIST_FIELDS = hashSetOf("serialVersionUID", "INSTANCE")

actual fun parseObject(obj: Any?): Value =
    valueObject(value = obj, visitedObjects = HashSet())

private fun valueObject(value: Any? = null, clazz: Class<*>? = null, visitedObjects: MutableSet<Any>): Value.Object =
    when {
        value != null -> valueObject(value = value, visitedObjects = visitedObjects)
        clazz != null -> valueObject(clazz = clazz)
        else -> Value.Object.Other(type = getFullTypeName(), value = null)
    }

private fun valueObject(value: Any, visitedObjects: MutableSet<Any>): Value.Object {
    if (value in visitedObjects) {
        return Value.Object.Unparsed(type = getFullTypeName(value = value), value = "Recursive reference")
    }

    visitedObjects += value
    try {
        return when (value) {
            is String -> Value.Object.String(value)
            is IntArray -> Value.Object.IntArray(value)
            is LongArray -> Value.Object.LongArray(value)
            is ShortArray -> Value.Object.ShortArray(value)
            is ByteArray -> Value.Object.ByteArray(value)
            is FloatArray -> Value.Object.FloatArray(value)
            is DoubleArray -> Value.Object.DoubleArray(value)
            is CharArray -> Value.Object.CharArray(value)
            is BooleanArray -> Value.Object.BooleanArray(value)
            is Array<*> -> array(value, visitedObjects)
            is Iterable<*> -> iterable(value, visitedObjects)
            is Map<*, *> -> map(value, visitedObjects)
            else -> other(value, visitedObjects)
        }
    } finally {
        visitedObjects -= value
    }
}

private fun valueObject(clazz: Class<*>): Value.Object =
    when {
        clazz == String::class.java -> Value.Object.String(null)
        clazz == IntArray::class.java -> Value.Object.IntArray(null)
        clazz == LongArray::class.java -> Value.Object.LongArray(null)
        clazz == ShortArray::class.java -> Value.Object.ShortArray(null)
        clazz == ByteArray::class.java -> Value.Object.ByteArray(null)
        clazz == FloatArray::class.java -> Value.Object.FloatArray(null)
        clazz == DoubleArray::class.java -> Value.Object.DoubleArray(null)
        clazz == CharArray::class.java -> Value.Object.CharArray(null)
        clazz == BooleanArray::class.java -> Value.Object.BooleanArray(null)
        clazz == Array<Any?>::class.java -> Value.Object.Array(getFullTypeName(clazz = Array<Any?>::class.java), null)
        Iterable::class.java.isAssignableFrom(clazz) -> Value.Object.Iterable(getFullTypeName(clazz = clazz), null)
        Map::class.java.isAssignableFrom(clazz) -> Value.Object.Map(getFullTypeName(clazz = clazz), null)
        else -> Value.Object.Other(getFullTypeName(clazz = clazz), null)
    }

private fun iterable(iterable: Iterable<*>, visitedObjects: MutableSet<Any>): Value.Object.Iterable =
    Value.Object.Iterable(
        type = getFullTypeName(iterable),
        value = iterable.map { valueObject(value = it, visitedObjects = visitedObjects) }
    )

private fun map(map: Map<*, *>, visitedObjects: MutableSet<Any>): Value.Object.Map =
    Value.Object.Map(
        type = getFullTypeName(map),
        value = run {
            val newMap = mutableMapOf<Value.Object, Value.Object>()
            map.forEach { (k, v) ->
                newMap[valueObject(value = k, visitedObjects = visitedObjects)] =
                    valueObject(value = v, visitedObjects = visitedObjects)
            }
            newMap
        }
    )

private fun array(array: Array<*>, visitedObjects: MutableSet<Any>): Value.Object.Array =
    Value.Object.Array(
        type = getFullTypeName(value = array),
        value = array.map { valueObject(value = it, visitedObjects = visitedObjects) }
    )

private fun other(obj: Any, visitedObjects: MutableSet<Any>): Value.Object.Other =
    when (obj) {
        is Throwable -> throwable(obj, visitedObjects)
        else -> otherDefault(obj, visitedObjects)
    }

private fun throwable(throwable: Throwable?, visitedObjects: MutableSet<Any>): Value.Object.Other =
    Value.Object.Other(
        type = getFullTypeName(throwable, Throwable::class.java),
        value = throwable?.let {
            mapOf(
                "message" to Value.Object.String(it.message),
                "cause" to throwable(it.cause, visitedObjects)
            )
        }
    )

private fun otherDefault(obj: Any, visitedObjects: MutableSet<Any>): Value.Object.Other {
    val map = mutableMapOf<String, Value>()

    obj.javaClass.allFields.forEach { field ->
        val isAccessible = field.isAccessible
        if (!isAccessible) {
            field.isAccessible = true
        }
        try {
            val fieldName = field.name
            if (fieldName.isAllowedFieldName()) {
                val value: Value =
                    when (field.type) {
                        Int::class.javaPrimitiveType -> Value.Primitive.Int(field.getInt(obj))
                        Long::class.javaPrimitiveType -> Value.Primitive.Long(field.getLong(obj))
                        Short::class.javaPrimitiveType -> Value.Primitive.Short(field.getShort(obj))
                        Byte::class.javaPrimitiveType -> Value.Primitive.Byte(field.getByte(obj))
                        Float::class.javaPrimitiveType -> Value.Primitive.Float(field.getFloat(obj))
                        Double::class.javaPrimitiveType -> Value.Primitive.Double(field.getDouble(obj))
                        Char::class.javaPrimitiveType -> Value.Primitive.Char(field.getChar(obj))
                        Boolean::class.javaPrimitiveType -> Value.Primitive.Boolean(field.getBoolean(obj))
                        else -> valueObject(field.get(obj), field.type, visitedObjects)
                    }

                map[fieldName] = value
            }
        } finally {
            if (!isAccessible) {
                field.isAccessible = false
            }
        }
    }

    return Value.Object.Other(type = getFullTypeName(value = obj), value = map)
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

fun getFullTypeName(value: Any? = null, clazz: Class<*>? = null): String {
    val valueClass = value?.javaClass ?: clazz ?: return "?"

    if (value != null) {
        if (valueClass.isArray) {
            return valueClass.simpleName.replaceFirst("[]", "[${java.lang.reflect.Array.getLength(value)}]")
        }

        if (value is Collection<*>) {
            return "${valueClass.toTypeNameWithGenerics(value)}(${value.size})"
        }
    }

    return valueClass.toTypeNameWithGenerics(value)
}

private fun Class<*>.toTypeNameWithGenerics(value: Any?): String =
    (value?.javaClass ?: this).let { clazz ->
        clazz
            .typeParameters
            .takeUnless(Array<*>::isEmpty)
            ?.joinToString(separator = ", ", prefix = "<", postfix = ">", transform = TypeVariable<*>::getName)
            ?.let { "${clazz.simpleName}$it" }
            ?: clazz.simpleName
    }
