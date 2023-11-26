package com.arkivanov.mvikotlin.core.utils

internal expect class Lock() {

    inline fun <T> synchronizedImpl(block: () -> T): T
}
