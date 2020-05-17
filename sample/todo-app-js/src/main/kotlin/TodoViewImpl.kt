import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import kotlin.properties.ObservableProperty

class TodoListViewImpl(
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

class TodoAddViewImpl(
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