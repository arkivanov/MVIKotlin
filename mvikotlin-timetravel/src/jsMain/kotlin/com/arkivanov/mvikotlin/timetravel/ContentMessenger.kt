package com.arkivanov.mvikotlin.timetravel

import kotlinx.browser.window
import org.w3c.dom.MessageEvent
import org.w3c.dom.events.Event

actual class ContentMessenger actual constructor() {

    actual fun subscribe(listener: (String) -> Unit): Cancellation {
        val callback: (Event) -> Unit =
            callback@{
                val event = it.unsafeCast<MessageEvent>()
                val message = event.data?.toString()

                if ((event.source == window) && (message != null)) {
                    listener(message)
                }
            }

        window.addEventListener(type = "message", callback = callback)

        return Cancellation { window.removeEventListener(type = "message", callback = callback) }
    }

    actual fun send(message: String) {
        window.postMessage(message = message, targetOrigin = "*")
    }
}
