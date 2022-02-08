@file:JsQualifier("chrome.runtime")

package chrome.runtime

external val onConnect: OnConnect

external interface Port {
    val onDisconnect: OnDisconnect
    val onMessage: OnMessage

    fun disconnect()

    fun postMessage(message: Any)
}

external interface OnDisconnect {
    fun addListener(callback: (Port) -> Unit)
}

external interface OnMessage {
    fun addListener(callback: (message: Any, Port) -> Unit)
}

external interface OnConnect {
    fun addListener(callback: (Port) -> Unit)
    fun removeListener(callback: (Port) -> Unit)
}
