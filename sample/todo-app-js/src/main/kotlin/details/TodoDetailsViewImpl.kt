package details

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import root.debugLog

class TodoDetailsViewImpl(
    private val updateState: (TodoDetailsView.Model) -> Unit
) : BaseMviView<TodoDetailsView.Model, TodoDetailsView.Event>(), TodoDetailsView {

    override fun render(model: TodoDetailsView.Model) {
        debugLog("list updateState")
        updateState(model)
    }

    fun dispatchEvent(event: TodoDetailsView.Event) {
        debugLog("list dispatch event")
        super.dispatch(event)
    }
}