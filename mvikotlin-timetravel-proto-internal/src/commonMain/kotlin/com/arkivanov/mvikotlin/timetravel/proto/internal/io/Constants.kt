package com.arkivanov.mvikotlin.timetravel.proto.internal.io


@Suppress("MagicNumber")
internal val FRAME_SEPARATOR: ByteArray = byteArrayOf(0, 127, 0, -128, 1, 127, -1, -128)

internal const val PROTO_VERSION: Int = 1
