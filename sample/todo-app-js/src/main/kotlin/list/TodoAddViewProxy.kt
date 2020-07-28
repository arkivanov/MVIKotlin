package list

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView

class TodoAddViewProxy(
    private val updateState: (TodoAddView.Model) -> Unit
) : BaseMviView<TodoAddView.Model, TodoAddView.Event>(), TodoAddView {

    override fun render(model: TodoAddView.Model) {
        updateState(model)
    }
}
