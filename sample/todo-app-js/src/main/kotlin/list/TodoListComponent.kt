package list

import FrameworkType
import LifecycleConsumer
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoListCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoListReaktiveController
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mTypography
import react.*
import root.App
import root.App.TodoStyles.addCss
import root.App.TodoStyles.columnCss
import root.App.TodoStyles.listCss
import root.LifecycleWrapper
import root.debugLog
import styled.css
import styled.styledDiv

class TodoListParentComponent(props: TodoListParentProps) : RComponent<TodoListParentProps, TodoListParentState>(props),
    LifecycleConsumer<TodoListController.Input> {

    private val listViewDelegate = TodoListViewProxy(updateState = ::updateState)
    private val addViewDelegate = TodoAddViewProxy(updateState = ::updateState)
    private val lifecycleWrapper = LifecycleWrapper()
    private lateinit var controller: TodoListController
    override lateinit var input: (TodoListController.Input) -> Unit
    override val lifecycle: Lifecycle = lifecycleWrapper.lifecycle

    init {
        debugLog("init")
        state = TodoListParentState(TodoListView.Model(listOf()), TodoAddView.Model(""))
    }

    override fun componentWillReceiveProps(nextProps: TodoListParentProps) {
        debugLog("componentWillReceiveProps")
        if (nextProps != props) {
            nextProps.dependencies.input?.let(input)
        }
    }

    override fun componentWillMount() {
        debugLog("componentWillMount")
    }

    override fun componentDidMount() {
        lifecycleWrapper.start()
        controller = createController()
        input = controller.input
        debugLog("componentDidMount")
        controller.onViewCreated(
            listViewDelegate,
            addViewDelegate,
            lifecycle,
            props.dependencies.output
        )
    }

    private fun createController(): TodoListController {
        val dependencies = props.dependencies
        val todoListControllerDependencies =
            object : TodoListController.Dependencies, Dependencies by dependencies {
                override val lifecycle: Lifecycle = lifecycleWrapper.lifecycle
            }

        return when (dependencies.frameworkType) {
            FrameworkType.REAKTIVE -> TodoListReaktiveController(todoListControllerDependencies)
            FrameworkType.COROUTINES -> TodoListCoroutinesController(todoListControllerDependencies)
        }
    }

    override fun componentWillUpdate(nextProps: TodoListParentProps, nextState: TodoListParentState) {
        debugLog("componentWillUpdate")
    }

    private fun updateState(newModel: TodoListView.Model) {
        debugLog("updateState List")
        setState { listModel = newModel }
    }

    private fun updateState(newModel: TodoAddView.Model) {
        debugLog("updateState Add")
        setState { addModel = newModel }
    }

    override fun RBuilder.render() {
        debugLog("List render")
        mContainer {
            attrs {
                component = "main"
                maxWidth = "xs"
            }
            styledDiv {
                css(columnCss)
                mTypography("Todos", variant = MTypographyVariant.h2) {
                    css(App.TodoStyles.headerMarginCss)
                }
                styledDiv {
                    css(addCss)
                    addTodo(
                        textValue = state.addModel.text,
                        onTextChanged = { addViewDelegate.dispatchEvent(TodoAddView.Event.TextChanged(it)) },
                        onAddClick = { addViewDelegate.dispatchEvent(TodoAddView.Event.AddClicked) }
                    )
                }
                styledDiv {
                    css(listCss)
                    listTodo(
                        todos = state.listModel.items,
                        onClick = { listViewDelegate.dispatchEvent(TodoListView.Event.ItemClicked(it)) },
                        onDoneClick = { listViewDelegate.dispatchEvent(TodoListView.Event.ItemDoneClicked(it)) },
                        onDeleteClick = { listViewDelegate.dispatchEvent(TodoListView.Event.ItemDeleteClicked(it)) }
                    )
                }
            }
        }

    }

    override fun componentWillUnmount() {
        debugLog("componentWillUnmount")
        lifecycleWrapper.stop()
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
        val output: (TodoListController.Output) -> Unit
        val input: TodoListController.Input?
    }

}

interface TodoListParentProps : RProps {
    var dependencies: TodoListParentComponent.Dependencies
}

class TodoListParentState(
    var listModel: TodoListView.Model,
    var addModel: TodoAddView.Model
) : RState

fun RBuilder.todoContainer(
    dependencies: TodoListParentComponent.Dependencies
) =
    child(TodoListParentComponent::class) {
        attrs.dependencies = dependencies
    }