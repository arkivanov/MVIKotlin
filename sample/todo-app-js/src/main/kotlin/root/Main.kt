package root
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

private class Application {
    fun start() {
        window.onload = {
            render(document.getElementById("app")) {
                app()
            }
        }
    }
}

fun main(){
    Application().start()
}

fun Any.debugLog(text: String?) {
    if (text.isNullOrEmpty().not()) console.log("${this::class.simpleName?.toUpperCase()}: $text")
}