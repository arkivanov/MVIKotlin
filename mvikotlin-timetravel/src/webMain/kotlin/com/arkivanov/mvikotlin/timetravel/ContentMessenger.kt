package com.arkivanov.mvikotlin.timetravel

expect class ContentMessenger() {

    fun subscribe(listener: (String) -> Unit): Cancellation
    fun send(message: String)
}
