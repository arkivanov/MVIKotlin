package root

import FrameworkType
import com.arkivanov.mvikotlin.core.store.StoreFactory
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

abstract class App : RComponent<AppProps, AppState>() {

    private var themeColor = "light"

    init {
        state = AppState(todoId = "", listInput = null)
    }

    override fun RBuilder.render() {
        mCssBaseline()
        @Suppress("UnsafeCastFromDynamic")
        val themeOptions: ThemeOptions = js("({palette: { type: 'placeholder', primary: {main: 'placeholder'}}})")
        themeOptions.palette?.type = themeColor
        themeOptions.palette?.primary.main = "#e91e63"
        themeOptions.spacing = 1

        mThemeProvider(createMuiTheme(themeOptions)) {
            todoContainer(
                dependencies = object : TodoListParentComponent.Dependencies, Dependencies by props.dependecies {
                    override val output = ::listOutput
                    override val input = state.listInput
                }
            )
            if (state.todoId.isNotEmpty())
                todoDetails(
                    dependencies = object : TodoDetailsComponent.Dependencies, Dependencies by props.dependecies {
                        override val todoId: String = state.todoId
                        override val output: (TodoDetailsController.Output) -> Unit = ::detailsOutput
                        override val onClose: () -> Unit = ::closeDetails
                    }
                )
        }

    }

    private fun listOutput(output: TodoListController.Output) {
        when (output) {
            is TodoListController.Output.ItemSelected -> setState { todoId = output.id }
        }
    }

    private fun closeDetails() {
        setState { todoId = "" }
    }

    private fun updateListInput(input: TodoListController.Input) {
        setState { listInput = input }
    }

    private fun detailsOutput(output: TodoDetailsController.Output) {
        when (output) {
            TodoDetailsController.Output.Finished -> closeDetails()
            is TodoDetailsController.Output.ItemChanged ->
                updateListInput(TodoListController.Input.ItemChanged(id = output.id, data = output.data))
            is TodoDetailsController.Output.ItemDeleted ->
                updateListInput(TodoListController.Input.ItemDeleted(id = output.id))
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
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
    }
}

class AppState(
    var todoId: String,
    var listInput: TodoListController.Input?
) : RState

interface AppProps : RProps {
    var dependecies: App.Dependencies
}

fun RBuilder.app(dependencies: App.Dependencies) = child(App::class) {
    attrs.dependecies = dependencies
}