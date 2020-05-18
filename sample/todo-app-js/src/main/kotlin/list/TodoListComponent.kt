package list

import TodoDatabaseImpl
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListControllerDeps
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoAddView
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoListView
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoListReaktiveController
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.spacingUnits
import kotlinx.css.*
import list.TodoListParentComponent.TodoComponentStyles.listCss
import react.*
import root.LifecycleWrapper
import root.debugLog
import styled.StyleSheet
import styled.css
import styled.styledDiv

class TodoListParentComponent : RComponent<TodoListParentProps, TodoListParentState>() {

    private val listViewDelegate = TodoListViewProxy(updateState = ::updateState)
    private val addViewDelegate = TodoAddViewProxy(updateState = ::updateState)
    private val lifecycleWrapper = LifecycleWrapper()
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
                mTypography("Todos", variant = MTypographyVariant.h2)
                styledDiv {
                    css(TodoComponentStyles.addCss)
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
        lifecycleWrapper.stop()
    }

    private object TodoComponentStyles : StyleSheet("TodoComponentStyles", isStatic = true) {
        val addCss by css {
            display = Display.inlineFlex
            width = 100.pct
            padding(2.spacingUnits)
        }

        val listCss by css {
            width = 100.pct
        }
    }

}

interface TodoListParentProps : RProps

class TodoListParentState(
    var listModel: TodoListView.Model,
    var addModel: TodoAddView.Model
) : RState

fun RBuilder.todoContainer() = child(TodoListParentComponent::class) {}