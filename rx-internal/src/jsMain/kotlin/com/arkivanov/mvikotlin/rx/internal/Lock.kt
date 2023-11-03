package com.arkivanov.mvikotlin.rx.internal

actual class Lock actual constructor() {

    actual inline fun <T> synchronizedImpl(block: () -> T): T =
        block()
}
