package list

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import root.debugLog

class TodoAddViewProxy(
    private val updateState: (TodoAddView.Model) -> Unit
) : BaseMviView<TodoAddView.Model, TodoAddView.Event>(), TodoAddView {

    override fun render(model: TodoAddView.Model) {
        debugLog("add updateState")
        updateState(model)
    }

    fun dispatchEvent(event: TodoAddView.Event) {
        debugLog("add dispatch event")
        super.dispatch(event)
    }
}