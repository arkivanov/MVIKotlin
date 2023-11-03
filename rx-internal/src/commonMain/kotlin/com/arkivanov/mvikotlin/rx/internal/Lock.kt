package com.arkivanov.mvikotlin.rx.internal

expect class Lock() {

    inline fun <T> synchronizedImpl(block: () -> T): T
}
