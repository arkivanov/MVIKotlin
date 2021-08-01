package com.arkivanov.mvikotlin.utils.internal

expect class IsolatedRef<out T : Any>(value: T) {

    val value: T
}
