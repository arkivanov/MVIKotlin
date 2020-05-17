import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListControllerDeps
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoListReaktiveController
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.spacingUnits
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

class TodoListParentComponent : RComponent<TodoListParentProps, TodoListParentState>() {

    private val listViewDelegate = TodoListViewImpl(updateState = ::updateState)
    private val addViewDelegate = TodoAddViewImpl(updateState = ::updateState)
    private val lifecycleWrapper = TodoLifecycleWrapper()
    private val controller = TodoListReaktiveController(
        dependencies = TodoListControllerDeps(
            DefaultStoreFactory,
            TodoDatabaseImpl(),
            lifecycleWrapper.lifecycle
        )
    )

    init {
        state = TodoListParentState(TodoListView.Model(listOf()), TodoAddView.Model(""))
    }

    override fun componentWillMount() {
        lifecycleWrapper.start()
    }

    override fun componentDidMount() {
        controller.onViewCreated(
            listViewDelegate,
            addViewDelegate,
            lifecycleWrapper.lifecycle,
            output = { debugLog("output income") })
    }

    private fun updateState(newModel: TodoListView.Model) {
        setState { listModel = newModel }
    }

    private fun updateState(newModel: TodoAddView.Model) {
        setState { addModel = newModel }
    }

    override fun RBuilder.render() {
        debugLog(state.listModel.toString())
        mCssBaseline()
        mContainer {
            attrs {
                component = "main"
                maxWidth = "xs"
            }
            styledDiv {
                css {
                    marginTop = 9.spacingUnits
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = Align.center
                }
                mTypography("todo here")
            }
        }

    }

    override fun componentWillUnmount() {
        lifecycleWrapper.stop()
    }

}

interface TodoListParentProps : RProps

class TodoListParentState(
    var listModel: TodoListView.Model,
    var addModel: TodoAddView.Model
) : RState

fun RBuilder.todoContainer() = child(TodoListParentComponent::class) {}