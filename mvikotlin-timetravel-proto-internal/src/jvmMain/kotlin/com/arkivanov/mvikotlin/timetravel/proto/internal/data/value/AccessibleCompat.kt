package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.reflect.Field

private var isCanAccessAvailable = true
private var isTrySetAccessibleAvailable = true

internal actual fun Field.canAccessCompat(obj: Any?): Boolean {
    if (isCanAccessAvailable) {
        try {
            return canAccess(obj)
        } catch (ignored: NoSuchMethodError) {
            isCanAccessAvailable = false
        }
    }

    @Suppress("DEPRECATION")
    return isAccessible
}

internal actual fun Field.trySetAccessibleCompat(): Boolean {
    if (isTrySetAccessibleAvailable) {
        try {
            return trySetAccessible()
        } catch (ignored: SecurityException) {
            return false
        } catch (ignored: NoSuchMethodError) {
            isTrySetAccessibleAvailable = false
        }
    }

    return try {
        isAccessible = true
        true
    } catch (ignored: SecurityException) {
        false
    }
}
