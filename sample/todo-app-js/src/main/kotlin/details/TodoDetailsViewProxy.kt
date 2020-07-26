package details

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView

class TodoDetailsViewProxy(
    private val updateState: (TodoDetailsView.Model) -> Unit
) : BaseMviView<TodoDetailsView.Model, TodoDetailsView.Event>(), TodoDetailsView {

    override fun render(model: TodoDetailsView.Model) {
        updateState(model)
    }
}
