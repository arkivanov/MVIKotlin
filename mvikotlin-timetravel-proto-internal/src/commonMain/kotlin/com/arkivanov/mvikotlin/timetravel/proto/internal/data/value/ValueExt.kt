package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

val ParsedValue.type: String
    get() =
        when (this) {
            is ParsedValue.Primitive.Int -> "Int"
            is ParsedValue.Primitive.Long -> "Long"
            is ParsedValue.Primitive.Short -> "Short"
            is ParsedValue.Primitive.Byte -> "Byte"
            is ParsedValue.Primitive.Float -> "Float"
            is ParsedValue.Primitive.Double -> "Double"
            is ParsedValue.Primitive.Char -> "Char"
            is ParsedValue.Primitive.Boolean -> "Boolean"
            is ParsedValue.Object.Int -> "Int"
            is ParsedValue.Object.Long -> "Long"
            is ParsedValue.Object.Short -> "Short"
            is ParsedValue.Object.Byte -> "Byte"
            is ParsedValue.Object.Float -> "Float"
            is ParsedValue.Object.Double -> "Double"
            is ParsedValue.Object.Char -> "Char"
            is ParsedValue.Object.Boolean -> "Boolean"
            is ParsedValue.Object.String -> "String"
            is ParsedValue.Object.IntArray -> "IntArray"
            is ParsedValue.Object.LongArray -> "LongArray"
            is ParsedValue.Object.ShortArray -> "ShortArray"
            is ParsedValue.Object.ByteArray -> "ByteArray"
            is ParsedValue.Object.FloatArray -> "FloatArray"
            is ParsedValue.Object.DoubleArray -> "DoubleArray"
            is ParsedValue.Object.CharArray -> "CharArray"
            is ParsedValue.Object.BooleanArray -> "BooleanArray"
            is ParsedValue.Object.Array -> type
            is ParsedValue.Object.Iterable -> type
            is ParsedValue.Object.Map -> type
            is ParsedValue.Object.Other -> type
            is ParsedValue.Object.Unparsed -> type
        }
