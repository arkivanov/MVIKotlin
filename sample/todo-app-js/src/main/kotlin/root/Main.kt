package root

import FrameworkType
import TodoDatabaseImpl
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import mFrameworkType
import react.dom.render
import storeFactoryInstance
import kotlin.browser.document
import kotlin.browser.window

private class Application {

    fun start() {
        window.onload = {
            render(document.getElementById("app")) {
                app(object : App.Dependencies {
                    override val storeFactory: StoreFactory = storeFactoryInstance
                    override val database: TodoDatabase = TodoDatabaseImpl()
                    override val frameworkType: FrameworkType = mFrameworkType
                })
            }
        }
    }

}

fun main() {
    Application().start()
}

fun Any.debugLog(text: String?) {
    if (text.isNullOrEmpty().not()) console.log("${this::class.simpleName?.toUpperCase()}: $text")
}
