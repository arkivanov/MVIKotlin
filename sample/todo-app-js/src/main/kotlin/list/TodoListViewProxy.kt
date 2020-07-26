package list

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView

class TodoListViewProxy(
    private val updateState: (TodoListView.Model) -> Unit
) : BaseMviView<TodoListView.Model, TodoListView.Event>(), TodoListView {

    override fun render(model: TodoListView.Model) {
        updateState(model)
    }
}
