package com.arkivanov.mvikotlin.utils.internal

actual class IsolatedRef<out T : Any> actual constructor(value: T) {

    actual val valueOrNull: T? = value
}
