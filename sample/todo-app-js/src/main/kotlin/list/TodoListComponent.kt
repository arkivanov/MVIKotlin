package list

import FrameworkType
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
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
import react.State
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
        state = js("{}") as TodoListParentState
        state.listModel = TodoListView.Model(listOf())
        state.addModel = TodoAddView.Model("")
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
                override val instanceKeeper: InstanceKeeper = InstanceKeeperDispatcher()
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

external interface TodoListParentProps : RProps {
    var dependencies: TodoListParentComponent.Dependencies
}

external interface TodoListParentState : State {
    var listModel: TodoListView.Model
    var addModel: TodoAddView.Model
}

fun RBuilder.todoContainer(dependencies: TodoListParentComponent.Dependencies) {
    child(TodoListParentComponent::class) {
        attrs.dependencies = dependencies
    }
}
