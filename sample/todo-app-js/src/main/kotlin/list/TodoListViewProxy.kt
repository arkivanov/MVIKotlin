package list

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import root.debugLog

class TodoListViewProxy(
    private val updateState: (TodoListView.Model) -> Unit
) : BaseMviView<TodoListView.Model, TodoListView.Event>(), TodoListView {

    override fun render(model: TodoListView.Model) {
        debugLog("list updateState")
        updateState(model)
    }

    fun dispatchEvent(event: TodoListView.Event) {
        debugLog("list dispatch event")
        super.dispatch(event)
    }
}