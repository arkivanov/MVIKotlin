package details

import FrameworkType
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoDetailsCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoDetailsReaktiveController
import com.ccfraser.muirwik.components.MTypographyVariant
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.form.MFormControlVariant
import com.ccfraser.muirwik.components.mCheckboxWithLabel
import com.ccfraser.muirwik.components.mContainer
import com.ccfraser.muirwik.components.mTextFieldMultiLine
import com.ccfraser.muirwik.components.mTypography
import com.ccfraser.muirwik.components.targetInputValue
import react.Props
import react.RBuilder
import react.RComponent
import react.State
import react.setState
import root.App.TodoStyles.columnCss
import root.App.TodoStyles.detailsButtonsCss
import root.App.TodoStyles.detailsInputCss
import root.App.TodoStyles.headerMarginCss
import root.appBarWithButton
import styled.css
import styled.styledDiv

class TodoDetailsComponent(props: TodoDetailsParentProps) :
    RComponent<TodoDetailsParentProps, TodoDetailsParentState>(props) {

    private val detailsViewProxy = TodoDetailsViewProxy(::updateState)
    private val lifecycleRegistry = LifecycleRegistry()
    private lateinit var controller: TodoDetailsController

    init {
        state = js("{}") as TodoDetailsParentState
        state.model = TodoDetailsView.Model("", false)
    }

    override fun componentDidMount() {
        lifecycleRegistry.resume()
        controller = createController()
        controller.onViewCreated(detailsViewProxy, lifecycleRegistry)
    }

    private fun createController(): TodoDetailsController {
        val dependencies = props.dependencies
        val todoDetailsControllerDependencies =
            object : TodoDetailsController.Dependencies, Dependencies by dependencies {
                override val lifecycle: Lifecycle = lifecycleRegistry
                override val itemId: String = dependencies.todoId
            }

        return when (dependencies.frameworkType) {
            FrameworkType.REAKTIVE ->
                TodoDetailsReaktiveController(todoDetailsControllerDependencies)
            FrameworkType.COROUTINES ->
                TodoDetailsCoroutinesController(todoDetailsControllerDependencies)
        }
    }

    private fun updateState(newModel: TodoDetailsView.Model) {
        setState { model = newModel }
    }

    override fun RBuilder.render() {
        val model = state.model
        mDialog(
            open = true,
            fullScreen = true,
            onClose = { _, _ -> handleClose() }
        ) {
            appBarWithButton(icon = "close", onIconClick = ::handleClose)
            mContainer {
                attrs {
                    component = "main"
                    maxWidth = "xs"
                }
                styledDiv {
                    css(columnCss)
                    mTypography(text = "Details", variant = MTypographyVariant.h2) {
                        css(headerMarginCss)
                    }
                    styledDiv {
                        css(detailsInputCss)
                        mTextFieldMultiLine(
                            variant = MFormControlVariant.outlined,
                            value = model.text,
                            label = "add todo text here",
                            fullWidth = true,
                            onChange = {
                                detailsViewProxy.dispatch(
                                    TodoDetailsView.Event.TextChanged(it.targetInputValue)
                                )
                            }
                        )
                    }
                    styledDiv {
                        css(detailsButtonsCss)
                        mCheckboxWithLabel(
                            label = "complete",
                            checked = model.isDone,
                            onChange = { _, _ ->
                                detailsViewProxy.dispatch(
                                    TodoDetailsView.Event.DoneClicked
                                )
                            }
                        )
                        mIconButton(
                            iconName = "delete",
                            onClick = {
                                detailsViewProxy.dispatch(
                                    TodoDetailsView.Event.DeleteClicked
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    private fun handleClose() {
        props.dependencies.detailsOutput(TodoDetailsController.Output.Finished)
    }

    override fun componentWillUnmount() {
        lifecycleRegistry.destroy()
    }

    interface Dependencies {
        val storeFactory: StoreFactory
        val database: TodoDatabase
        val frameworkType: FrameworkType
        val detailsOutput: (TodoDetailsController.Output) -> Unit
        val todoId: String
    }

}

external interface TodoDetailsParentState : State {
    var model: TodoDetailsView.Model
}

external interface TodoDetailsParentProps : Props {
    var dependencies: TodoDetailsComponent.Dependencies
}

fun RBuilder.todoDetails(
    dependencies: TodoDetailsComponent.Dependencies
) {
    child(TodoDetailsComponent::class) {
        attrs.dependencies = dependencies
    }
}
