package com.arkivanov.mvikotlin.timetravel.proto.internal

fun ByteArray.convertToString(): String =
    joinToString(separator = ",")

fun String.convertToByteArray(): ByteArray =
    splitToSequence(",")
        .map(String::toByte)
        .toList()
        .toByteArray()
