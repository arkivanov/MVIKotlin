package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.RuntimeException
import java.lang.reflect.Field

internal actual fun Field.canAccessCompat(obj: Any?): Boolean = isAccessible

internal actual fun Field.trySetAccessibleCompat(): Boolean =
    try {
        isAccessible = true
        true
    } catch (ignored: SecurityException) {
        false
    } catch (ignored: RuntimeException) {
        // Not possible on Android in general, needed only for unit tests
        false
    }
