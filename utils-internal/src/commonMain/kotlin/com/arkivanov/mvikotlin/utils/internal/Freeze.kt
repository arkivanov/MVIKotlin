package com.arkivanov.mvikotlin.utils.internal

expect fun <T : Any> T.ensureNeverFrozen(): T

expect fun <T : Any> T.freeze(): T

expect val Any.isFrozen: Boolean
