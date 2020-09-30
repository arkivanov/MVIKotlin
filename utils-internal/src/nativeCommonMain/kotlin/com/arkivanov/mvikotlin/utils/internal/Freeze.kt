package com.arkivanov.mvikotlin.utils.internal

import kotlin.native.concurrent.ensureNeverFrozen as nativeEnsureNeverFrozen
import kotlin.native.concurrent.freeze as nativeFreeze
import kotlin.native.concurrent.isFrozen as nativeIsFrozen

actual fun <T : Any> T.ensureNeverFrozen(): T {
    nativeEnsureNeverFrozen()

    return this
}

actual fun <T : Any> T.freeze(): T = nativeFreeze()

actual val Any.isFrozen: Boolean get() = nativeIsFrozen
