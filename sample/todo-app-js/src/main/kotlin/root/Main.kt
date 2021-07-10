package root

import FrameworkType
import TodoDatabaseImpl
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import kotlinx.browser.document
import mFrameworkType
import react.dom.render
import storeFactoryInstance

fun main() {
    render(document.getElementById("app")) {
        app(object : App.Dependencies {
            override val storeFactory: StoreFactory = storeFactoryInstance
            override val database: TodoDatabase = TodoDatabaseImpl()
            override val frameworkType: FrameworkType = mFrameworkType
        })
    }
}

fun Any.debugLog(text: String?) {
    if (text.isNullOrEmpty().not()) console.log("${this::class.simpleName?.uppercase()}: $text")
}
