package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.reflect.Field

internal actual fun Field.canAccessCompat(obj: Any?): Boolean = isAccessible

internal actual fun Field.trySetAccessibleCompat(): Boolean =
    try {
        isAccessible = true
        true
    } catch (ignored: SecurityException) {
        false
    }
