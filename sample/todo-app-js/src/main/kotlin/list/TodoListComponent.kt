package list

import FrameworkType
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.core.lifecycle.destroy
import com.arkivanov.mvikotlin.core.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.keepers.instancekeeper.DefaultInstanceKeeper
import com.arkivanov.mvikotlin.keepers.instancekeeper.InstanceKeeper
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoListCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoListReaktiveController
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mTypography
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.setState
import root.App.TodoStyles.addCss
import root.App.TodoStyles.columnCss
import root.App.TodoStyles.headerMarginCss
import root.App.TodoStyles.listCss
import root.debugLog
import styled.css
import styled.styledDiv

class TodoListParentComponent(props: TodoListParentProps) : RComponent<TodoListParentProps, TodoListParentState>(props) {

    private val listViewDelegate = TodoListViewProxy(updateState = ::updateState)
    private val addViewDelegate = TodoAddViewProxy(updateState = ::updateState)
    private val lifecycleRegistry = LifecycleRegistry()
    private lateinit var controller: TodoListController

    init {
        state = TodoListParentState(TodoListView.Model(listOf()), TodoAddView.Model(""))
    }

    override fun componentDidMount() {
        lifecycleRegistry.resume()
        controller = createController()
        val dependencies = props.dependencies
        val disposable = dependencies.listInput(observer(onNext = controller.input))
        lifecycleRegistry.doOnDestroy(disposable::dispose)
        controller.onViewCreated(listViewDelegate, addViewDelegate, lifecycleRegistry)
    }

    private fun createController(): TodoListController {
        val dependencies = props.dependencies
        val todoListControllerDependencies =
            object : TodoListController.Dependencies, Dependencies by dependencies {
                override val lifecycle: Lifecycle = lifecycleRegistry
                override val instanceKeeper: InstanceKeeper = DefaultInstanceKeeper()
            }

        return when (dependencies.frameworkType) {
            FrameworkType.REAKTIVE -> TodoListReaktiveController(todoListControllerDependencies)
            FrameworkType.COROUTINES -> TodoListCoroutinesController(todoListControllerDependencies)
        }
    }

    private fun updateState(newModel: TodoListView.Model) {
        setState { listModel = newModel }
    }

    private fun updateState(newModel: TodoAddView.Model) {
        setState { addModel = newModel }
    }

    override fun RBuilder.render() {
        mContainer {
            attrs {
                component = "main"
                maxWidth = "xs"
            }
            styledDiv {
                css(columnCss)
                mTypography("Todos", variant = MTypographyVariant.h2) {
                    css(headerMarginCss)
                }
                styledDiv {
                    css(addCss)
                    addTodo(
                        textValue = state.addModel.text,
                        onTextChanged = { addViewDelegate.dispatch(TodoAddView.Event.TextChanged(it)) },
                        onAddClick = { addViewDelegate.dispatch(TodoAddView.Event.AddClicked) }
                    )
                }
                styledDiv {
                    css(listCss)
                    listTodo(
                        todos = state.listModel.items,
                        onClick = { listViewDelegate.dispatch(TodoListView.Event.ItemClicked(it)) },
                        onDoneClick = { listViewDelegate.dispatch(TodoListView.Event.ItemDoneClicked(it)) },
                        onDeleteClick = { listViewDelegate.dispatch(TodoListView.Event.ItemDeleteClicked(it)) }
                    )
                }
            }
        }
    }

    override fun componentWillUnmount() {
        debugLog("componentWillUnmount")
        lifecycleRegistry.destroy()
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
        val listInput: (Observer<TodoListController.Input>) -> Disposable
        val listOutput: (TodoListController.Output) -> Unit
    }
}

interface TodoListParentProps : RProps {
    var dependencies: TodoListParentComponent.Dependencies
}

class TodoListParentState(
    var listModel: TodoListView.Model,
    var addModel: TodoAddView.Model
) : RState

fun RBuilder.todoContainer(dependencies: TodoListParentComponent.Dependencies) = child(TodoListParentComponent::class) {
    attrs.dependencies = dependencies
}
