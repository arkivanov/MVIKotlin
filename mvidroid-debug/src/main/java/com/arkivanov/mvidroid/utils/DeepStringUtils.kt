package com.arkivanov.mvidroid.utils

import org.json.JSONObject
import java.lang.reflect.Field
import java.lang.reflect.TypeVariable
import kotlin.math.min

private val BLACK_LIST_FIELDS = hashSetOf("serialVersionUID", "INSTANCE")

internal fun Any.toDeepString(mode: DeepStringMode, format: Boolean): String =
    toJsonValue(mode, HashSet()).let {
        when (it) {
            is JsonValue.Integer -> it.value.toString()
            is JsonValue.Long -> it.value.toString()
            is JsonValue.Double -> it.value.toString()
            is JsonValue.Boolean -> it.value.toString()
            is JsonValue.String -> "\"${it.value}\""
            is JsonValue.JsonObject -> it.value.let { if (format) it.toString(2) else it.toString() }
        }
    }

private fun JSONObject.putValue(name: String, value: JsonValue?): JSONObject =
    when (value) {
        is JsonValue.Integer -> put(name, value.value)
        is JsonValue.Long -> put(name, value.value)
        is JsonValue.Double -> put(name, value.value)
        is JsonValue.Boolean -> put(name, value.value)
        is JsonValue.String -> put(name, value.value)
        is JsonValue.JsonObject -> put(name, value.value)
        null -> put(name, JSONObject.NULL)
    }

private fun Any.toJsonValue(mode: DeepStringMode, visitedObjects: MutableSet<Any>): JsonValue =
    if (visitedObjects.contains(this)) {
        JsonValue.String("Recursive reference")
    } else {
        visitedObjects.add(this)
        try {
            when (this) {
                is String -> JsonValue.String(if ((mode === DeepStringMode.SHORT) && (length > 64)) "${substring(0, 64)}…" else this)
                is Int -> JsonValue.Integer(this)
                is Long -> JsonValue.Long(this)
                is Float -> JsonValue.Double(this.toDouble())
                is Double -> JsonValue.Double(this)
                is Boolean -> JsonValue.Boolean(this)
                is Char -> JsonValue.String(this.toString())
                is Short -> JsonValue.Integer(this.toInt())
                is Byte -> JsonValue.Integer(this.toInt())
                is Enum<*> -> JsonValue.String(this.name)
                else -> JsonValue.JsonObject(toJson(mode, visitedObjects))
            }
        } finally {
            visitedObjects.remove(this)
        }
    }

private fun getArrayLimit(mode: DeepStringMode): Int? =
    when (mode) {
        DeepStringMode.SHORT -> 0
        DeepStringMode.MEDIUM -> 10
        DeepStringMode.FULL -> null
    }

private fun Any.toJson(mode: DeepStringMode, visitedObjects: MutableSet<Any>): JSONObject =
    JSONObject().also { json ->
        when (this) {
            is Iterable<*> -> json.putValues(iterator(), mode, visitedObjects)
            is Map<*, *> -> json.putValues(this, mode, visitedObjects)
            is Array<*> -> json.putValues(iterator(), mode, visitedObjects)
            is IntArray -> json.putValues(this, mode)
            is LongArray -> json.putValues(this, mode)
            is FloatArray -> json.putValues(this, mode)
            is DoubleArray -> json.putValues(this, mode)
            is BooleanArray -> json.putValues(this, mode)
            is CharArray -> json.putValues(this, mode)
            is ShortArray -> json.putValues(this, mode)
            is ByteArray -> json.putValues(this, mode)
            else -> json.putValues(this, mode, visitedObjects)
        }
    }

private fun JSONObject.putValues(iterator: Iterator<*>, mode: DeepStringMode, visitedObjects: MutableSet<Any>) {
    val limit: Int? = getArrayLimit(mode)
    var index = 0
    while (((limit == null) || (index < limit)) && iterator.hasNext()) {
        putTypedValue(index.toString(), iterator.next(), null, mode, visitedObjects)
        index++
    }
}

private fun JSONObject.putValues(map: Map<*, *>, mode: DeepStringMode, visitedObjects: MutableSet<Any>) {
    val limit: Int? = getArrayLimit(mode)
    var index = 0
    val iterator = map.entries.iterator()

    while (((limit == null) || (index < limit)) && iterator.hasNext()) {
        val (key: Any?, value: Any?) = iterator.next()
        val entryJson = JSONObject()
        entryJson.putTypedValue("key", key, null, mode, visitedObjects)
        entryJson.putTypedValue("value", value, null, mode, visitedObjects)
        put("$index", entryJson)
        index++
    }
}

private fun JSONObject.putTypedValue(prefix: String, value: Any?, valueClass: Class<*>?, mode: DeepStringMode, visitedObjects: MutableSet<Any>) {
    putValue("$prefix: ${getFullTypeName(value, valueClass)}", value?.toJsonValue(mode, visitedObjects))
}

private fun JSONObject.putValues(array: IntArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: LongArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: FloatArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: DoubleArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: BooleanArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: CharArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: ShortArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
    }
}

private fun JSONObject.putValues(array: ByteArray, mode: DeepStringMode) {
    for (index in 0 until (getArrayLimit(mode)?.let { min(it, array.size) } ?: array.size)) {
        put("$index", array[index])
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

private fun JSONObject.putValues(obj: Any, mode: DeepStringMode, visitedObjects: MutableSet<Any>) {
    if (obj is Throwable) {
        putTypedValue("message", obj.message, String::class.java, mode, visitedObjects)
        putTypedValue("cause", obj.cause, Throwable::class.java, mode, visitedObjects)
        if (mode === DeepStringMode.FULL) {
            putTypedValue("stackTrace", obj.stackTrace, null, mode, visitedObjects)
        }

        return
    }

    obj.javaClass.allFields.forEach { field ->
        val isAccessible = field.isAccessible
        if (!isAccessible) {
            field.isAccessible = true
        }
        try {
            val fieldName = field.name
            if (fieldName.isAllowedFieldName()) {
                when (field.type) {
                    Int::class.javaPrimitiveType -> put("$fieldName: int", field.getInt(obj))
                    Long::class.javaPrimitiveType -> put("$fieldName: long", field.getLong(obj))
                    Float::class.javaPrimitiveType -> put("$fieldName: float", field.getFloat(obj))
                    Double::class.javaPrimitiveType -> put("$fieldName: double", field.getDouble(obj))
                    Boolean::class.javaPrimitiveType -> put("$fieldName: boolean", field.getBoolean(obj))
                    Char::class.javaPrimitiveType -> put("$fieldName: char", field.getChar(obj))
                    Short::class.javaPrimitiveType -> put("$fieldName: short", field.getShort(obj))
                    Byte::class.javaPrimitiveType -> put("$fieldName: byte", field.getByte(obj))
                    else -> putTypedValue(fieldName, field.get(obj), field.type, mode, visitedObjects)
                }
            }
        } finally {
            if (!isAccessible) {
                field.isAccessible = false
            }
        }
    }
}

private fun String.isAllowedFieldName(): Boolean = !startsWith("$") && !BLACK_LIST_FIELDS.contains(this)

private fun getFullTypeName(value: Any?, valueClass: Class<*>? = null): String {
    val clazz = value?.javaClass ?: valueClass ?: return "?"

    if (value != null) {
        if (clazz.isArray) {
            return clazz.simpleName.replaceFirst("[]", "[${java.lang.reflect.Array.getLength(value)}]")
        }

        if (value is Collection<*>) {
            return "${clazz.toTypeNameWithGenerics(value)}(${value.size})"
        }
    }

    return clazz.toTypeNameWithGenerics(value)
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

internal enum class DeepStringMode {
    SHORT, MEDIUM, FULL
}

private sealed class JsonValue {
    class Integer(val value: kotlin.Int) : JsonValue()
    class Long(val value: kotlin.Long) : JsonValue()
    class Double(val value: kotlin.Double) : JsonValue()
    class Boolean(val value: kotlin.Boolean) : JsonValue()
    class String(val value: kotlin.String) : JsonValue()
    class JsonObject(val value: JSONObject) : JsonValue()
}
