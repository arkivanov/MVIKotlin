package com.arkivanov.mvikotlin.utils.internal

actual fun <T : Any> T.ensureNeverFrozen(): T = this

actual fun <T : Any> T.freeze(): T = this

actual val Any.isFrozen: Boolean get() = false
