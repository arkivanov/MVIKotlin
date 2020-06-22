package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

val Value.type: String
    get() =
        when (this) {
            is Value.Primitive.Int -> "Int"
            is Value.Primitive.Long -> "Long"
            is Value.Primitive.Short -> "Short"
            is Value.Primitive.Byte -> "Byte"
            is Value.Primitive.Float -> "Float"
            is Value.Primitive.Double -> "Double"
            is Value.Primitive.Char -> "Char"
            is Value.Primitive.Boolean -> "Boolean"
            is Value.Object.String -> "String"
            is Value.Object.IntArray -> "IntArray"
            is Value.Object.LongArray -> "LongArray"
            is Value.Object.ShortArray -> "ShortArray"
            is Value.Object.ByteArray -> "ByteArray"
            is Value.Object.FloatArray -> "FloatArray"
            is Value.Object.DoubleArray -> "DoubleArray"
            is Value.Object.CharArray -> "CharArray"
            is Value.Object.BooleanArray -> "BooleanArray"
            is Value.Object.Array -> type
            is Value.Object.Iterable -> type
            is Value.Object.Map -> type
            is Value.Object.Other -> type
            is Value.Object.Unparsed -> type
        }
