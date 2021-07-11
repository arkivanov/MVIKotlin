package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.lang.reflect.TypeVariable

private val BLACK_LIST_FIELDS = hashSetOf("serialVersionUID", "INSTANCE")

actual fun parseObject(obj: Any?): ParsedValue =
    valueObject(value = obj, visitedObjects = HashSet())

private fun valueObject(value: Any? = null, clazz: Class<*>? = null, visitedObjects: MutableSet<Any>): ParsedValue.Object =
    when {
        value != null -> valueObject(value = value, visitedObjects = visitedObjects)
        clazz != null -> valueObject(clazz = clazz)
        else -> ParsedValue.Object.Other(type = getFullTypeName(), value = null)
    }

private fun valueObject(value: Any, visitedObjects: MutableSet<Any>): ParsedValue.Object {
    if (value in visitedObjects) {
        return ParsedValue.Object.Unparsed(type = getFullTypeName(value = value), value = "Recursive reference")
    }

    visitedObjects += value
    try {
        return when (value) {
            is Int -> ParsedValue.Object.Int(value)
            is Long -> ParsedValue.Object.Long(value)
            is Short -> ParsedValue.Object.Short(value)
            is Byte -> ParsedValue.Object.Byte(value)
            is Float -> ParsedValue.Object.Float(value)
            is Double -> ParsedValue.Object.Double(value)
            is Char -> ParsedValue.Object.Char(value)
            is Boolean -> ParsedValue.Object.Boolean(value)
            is String -> ParsedValue.Object.String(value)
            is IntArray -> ParsedValue.Object.IntArray(value)
            is LongArray -> ParsedValue.Object.LongArray(value)
            is ShortArray -> ParsedValue.Object.ShortArray(value)
            is ByteArray -> ParsedValue.Object.ByteArray(value)
            is FloatArray -> ParsedValue.Object.FloatArray(value)
            is DoubleArray -> ParsedValue.Object.DoubleArray(value)
            is CharArray -> ParsedValue.Object.CharArray(value)
            is BooleanArray -> ParsedValue.Object.BooleanArray(value)
            is Array<*> -> array(value, visitedObjects)
            is Iterable<*> -> iterable(value, visitedObjects)
            is Map<*, *> -> map(value, visitedObjects)
            else -> other(value, visitedObjects)
        }
    } finally {
        visitedObjects -= value
    }
}

private fun valueObject(clazz: Class<*>): ParsedValue.Object =
    when {
        clazz == Int::class.java -> ParsedValue.Object.Int(null)
        clazz == Long::class.java -> ParsedValue.Object.Long(null)
        clazz == Short::class.java -> ParsedValue.Object.Short(null)
        clazz == Byte::class.java -> ParsedValue.Object.Byte(null)
        clazz == Float::class.java -> ParsedValue.Object.Float(null)
        clazz == Double::class.java -> ParsedValue.Object.Double(null)
        clazz == Char::class.java -> ParsedValue.Object.Char(null)
        clazz == Boolean::class.java -> ParsedValue.Object.Boolean(null)
        clazz == String::class.java -> ParsedValue.Object.String(null)
        clazz == IntArray::class.java -> ParsedValue.Object.IntArray(null)
        clazz == LongArray::class.java -> ParsedValue.Object.LongArray(null)
        clazz == ShortArray::class.java -> ParsedValue.Object.ShortArray(null)
        clazz == ByteArray::class.java -> ParsedValue.Object.ByteArray(null)
        clazz == FloatArray::class.java -> ParsedValue.Object.FloatArray(null)
        clazz == DoubleArray::class.java -> ParsedValue.Object.DoubleArray(null)
        clazz == CharArray::class.java -> ParsedValue.Object.CharArray(null)
        clazz == BooleanArray::class.java -> ParsedValue.Object.BooleanArray(null)
        clazz == Array<Any?>::class.java -> ParsedValue.Object.Array(getFullTypeName(clazz = Array<Any?>::class.java), null)
        Iterable::class.java.isAssignableFrom(clazz) -> ParsedValue.Object.Iterable(getFullTypeName(clazz = clazz), null)
        Map::class.java.isAssignableFrom(clazz) -> ParsedValue.Object.Map(getFullTypeName(clazz = clazz), null)
        else -> ParsedValue.Object.Other(getFullTypeName(clazz = clazz), null)
    }

private fun iterable(iterable: Iterable<*>, visitedObjects: MutableSet<Any>): ParsedValue.Object.Iterable =
    ParsedValue.Object.Iterable(
        type = getFullTypeName(iterable),
        value = iterable.map { valueObject(value = it, visitedObjects = visitedObjects) }
    )

private fun map(map: Map<*, *>, visitedObjects: MutableSet<Any>): ParsedValue.Object.Map =
    ParsedValue.Object.Map(
        type = getFullTypeName(map),
        value = run {
            val newMap = mutableMapOf<ParsedValue.Object, ParsedValue.Object>()
            map.forEach { (k, v) ->
                newMap[valueObject(value = k, visitedObjects = visitedObjects)] =
                    valueObject(value = v, visitedObjects = visitedObjects)
            }
            newMap
        }
    )

private fun array(array: Array<*>, visitedObjects: MutableSet<Any>): ParsedValue.Object.Array =
    ParsedValue.Object.Array(
        type = getFullTypeName(value = array),
        value = array.map { valueObject(value = it, visitedObjects = visitedObjects) }
    )

private fun other(obj: Any, visitedObjects: MutableSet<Any>): ParsedValue.Object.Other =
    when (obj) {
        is Throwable -> throwable(obj, visitedObjects)
        else -> otherDefault(obj, visitedObjects)
    }

private fun throwable(throwable: Throwable?, visitedObjects: MutableSet<Any>): ParsedValue.Object.Other =
    ParsedValue.Object.Other(
        type = getFullTypeName(throwable, Throwable::class.java),
        value = throwable?.let {
            mapOf(
                "message" to ParsedValue.Object.String(it.message),
                "cause" to throwable(it.cause, visitedObjects)
            )
        }
    )

private fun otherDefault(obj: Any, visitedObjects: MutableSet<Any>): ParsedValue.Object.Other {
    val fields = mutableMapOf<String, ParsedValue>()

    obj.javaClass.allFields.forEach { field ->
        if (field.isValidForParsing()) {
            @Suppress("DEPRECATION") // Required for Java 8
            val isAccessible = field.isAccessible
            if (isAccessible || field.trySetAccessibleCompat()) {
                try {
                    val fieldName = field.name
                    if (fieldName.isAllowedFieldName()) {
                        fields[fieldName] = field.getValue(obj, visitedObjects)
                    }
                } finally {
                    if (!isAccessible) {
                        field.isAccessible = false
                    }
                }
            }
        }
    }

    return ParsedValue.Object.Other(type = getFullTypeName(value = obj), value = fields)
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

private fun Field.getValue(obj: Any, visitedObjects: MutableSet<Any>): ParsedValue =
    when (type) {
        Int::class.javaPrimitiveType -> ParsedValue.Primitive.Int(getInt(obj))
        Long::class.javaPrimitiveType -> ParsedValue.Primitive.Long(getLong(obj))
        Short::class.javaPrimitiveType -> ParsedValue.Primitive.Short(getShort(obj))
        Byte::class.javaPrimitiveType -> ParsedValue.Primitive.Byte(getByte(obj))
        Float::class.javaPrimitiveType -> ParsedValue.Primitive.Float(getFloat(obj))
        Double::class.javaPrimitiveType -> ParsedValue.Primitive.Double(getDouble(obj))
        Char::class.javaPrimitiveType -> ParsedValue.Primitive.Char(getChar(obj))
        Boolean::class.javaPrimitiveType -> ParsedValue.Primitive.Boolean(getBoolean(obj))
        else -> valueObject(get(obj), type, visitedObjects)
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
    val valueClass = value?.javaClass ?: clazz ?: return "Object"

    if (valueClass.isArray) {
        return "Array<${valueClass.componentType?.getFixedName() ?: "T"}>"
    }

    return valueClass
        .typeParameters
        .takeUnless(Array<*>::isEmpty)
        ?.joinToString(separator = ", ", prefix = "${valueClass.getFixedName()}<", postfix = ">", transform = TypeVariable<*>::getName)
        ?: valueClass.getFixedName()
}

private fun Class<*>.getFixedName(): String =
    this.kotlin.simpleName ?: simpleName
