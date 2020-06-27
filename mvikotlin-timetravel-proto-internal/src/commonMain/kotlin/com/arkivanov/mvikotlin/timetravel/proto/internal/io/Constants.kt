package com.arkivanov.mvikotlin.timetravel.proto.internal.io

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
internal val FRAME_SEPARATOR = byteArrayOf(0, 127, 0, -128, 1, 127, -1, -128)
