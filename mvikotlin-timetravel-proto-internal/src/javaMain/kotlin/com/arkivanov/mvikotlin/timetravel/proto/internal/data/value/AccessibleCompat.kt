package com.arkivanov.mvikotlin.timetravel.proto.internal.data.value

import java.lang.reflect.Field

internal expect fun Field.canAccessCompat(obj: Any?): Boolean

internal expect fun Field.trySetAccessibleCompat(): Boolean
