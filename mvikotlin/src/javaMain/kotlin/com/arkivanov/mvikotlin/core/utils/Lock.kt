package com.arkivanov.mvikotlin.core.utils

internal actual class Lock actual constructor() {

    actual inline fun <T> synchronizedImpl(block: () -> T): T =
        synchronized(this, block)
}
