package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import kotlin.native.concurrent.SharedImmutable

@Suppress("MagicNumber")
@SharedImmutable
internal val FRAME_SEPARATOR: ByteArray = byteArrayOf(0, 127, 0, -128, 1, 127, -1, -128)

@SharedImmutable
internal val PROTO_VERSION: Int = 1
