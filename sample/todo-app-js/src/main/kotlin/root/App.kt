package root

import DEBUG
import Disposable
import FrameworkType
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.Observer
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoListController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.ccfraser.muirwik.components.mCssBaseline
import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.spacingUnits
import com.ccfraser.muirwik.components.styles.ThemeOptions
import com.ccfraser.muirwik.components.styles.createMuiTheme
import details.TodoDetailsComponent
import details.todoDetails
import kotlinx.css.*
import list.TodoListParentComponent
import list.todoContainer
import react.*
import styled.StyleSheet
import timetravel.timeTravel

abstract class App : RComponent<AppProps, AppState>() {

    private val themeColor = "light"
    private var listInputObserver: Observer<TodoListController.Input>? = null
    private val listInput: (Observer<TodoListController.Input>) -> Disposable = ::listInput
    private val listOutput: (TodoListController.Output) -> Unit = ::listOutput
    private val detailsOutput: (TodoDetailsController.Output) -> Unit = ::detailsOutput

    init {
        state = AppState(todoId = "", showDebugDrawer = false)
    }

    override fun RBuilder.render() {
        mCssBaseline()
        @Suppress("UnsafeCastFromDynamic")
        val themeOptions: ThemeOptions = js("({palette: { type: 'placeholder', primary: {main: 'placeholder'}}})")
        themeOptions.palette?.type = themeColor
        themeOptions.palette?.primary.main = "#e91e63"
        themeOptions.spacing = 1

        mThemeProvider(createMuiTheme(themeOptions)) {
            appBarWithButton("menu", onIconClick = { setState { showDebugDrawer = true } })
            todoContainer(
                dependencies = object : TodoListParentComponent.Dependencies, Dependencies by props.dependecies {
                    override val listInput: (Observer<TodoListController.Input>) -> Disposable = this@App.listInput
                    override val listOutput: (TodoListController.Output) -> Unit = this@App.listOutput
                }
            )
            if (state.todoId.isNotEmpty()) {
                todoDetails(
                    dependencies = object : TodoDetailsComponent.Dependencies, Dependencies by props.dependecies {
                        override val todoId: String = state.todoId
                        override val detailsOutput: (TodoDetailsController.Output) -> Unit = this@App.detailsOutput
                    }
                )
            }
            if (DEBUG && state.showDebugDrawer) {
                timeTravel(onClose = ::closeDebug)
            }
        }

    }

    private fun listInput(observer: Observer<TodoListController.Input>): Disposable {
        listInputObserver = observer

        return Disposable { listInputObserver = null }
    }

    private fun listOutput(output: TodoListController.Output) {
        when (output) {
            is TodoListController.Output.ItemSelected -> setState { todoId = output.id }
        }
    }

    private fun closeDebug() = setState { showDebugDrawer = false }
    private fun closeDetails() = setState { todoId = "" }


    private fun detailsOutput(output: TodoDetailsController.Output) {
        when (output) {
            TodoDetailsController.Output.Finished -> closeDetails()
            is TodoDetailsController.Output.ItemChanged ->
                listInputObserver?.onNext(TodoListController.Input.ItemChanged(id = output.id, data = output.data))
            is TodoDetailsController.Output.ItemDeleted ->
                listInputObserver?.onNext(TodoListController.Input.ItemDeleted(id = output.id))
        }
    }

    object TodoStyles : StyleSheet("TodoStyles", isStatic = true) {
        val addCss by css {
            display = Display.inlineFlex
            width = 100.pct
            padding(2.spacingUnits)
        }

        val listCss by css {
            width = 100.pct
        }

        val columnCss by css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = Align.center
        }

        val headerMarginCss by css {
            marginTop = 9.spacingUnits
        }

        val detailsButtonsCss by css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.spaceBetween
            alignItems = Align.center
            width = 100.pct
            paddingLeft = 2.spacingUnits
        }

        val detailsInputCss by css {
            width = 100.pct
            padding(2.spacingUnits)
        }

        val debugDrawerStyle by css {
            height = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            justifyContent = JustifyContent.spaceBetween
        }

        val debugButtonsContainerStyle by css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.spaceAround
        }

        val eventItemCss by css {
            overflow = Overflow.hidden
            textOverflow = TextOverflow.ellipsis
            put("-webkit-line-clamp", "3")
            put("display", "-webkit-box")
            put("-webkit-box-orient", "vertical")
        }

        val eventsContainerStyle by css {
            height = 100.pct
            overflow = Overflow.auto
            width = 320.px
        }
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
    }
}

class AppState(
    var todoId: String,
    var showDebugDrawer: Boolean
) : RState {
}

interface AppProps : RProps {
    var dependecies: App.Dependencies
}

fun RBuilder.app(dependencies: App.Dependencies) = child(App::class) {
    attrs.dependecies = dependencies
}
