package details

import ANIMATION_DURATION
import FrameworkType
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.todo.common.controller.TodoDetailsController
import com.arkivanov.mvikotlin.sample.todo.common.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.todo.common.view.TodoDetailsView
import com.arkivanov.mvikotlin.sample.todo.coroutines.controller.TodoDetailsCoroutinesController
import com.arkivanov.mvikotlin.sample.todo.reaktive.controller.TodoDetailsReaktiveController
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mIconButton
import com.ccfraser.muirwik.components.dialog.mDialog
import com.ccfraser.muirwik.components.form.MFormControlVariant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import list.SlideUpTransitionComponent
import react.*
import root.App.TodoStyles.columnCss
import root.App.TodoStyles.detailsButtonsCss
import root.App.TodoStyles.detailsInputCss
import root.App.TodoStyles.headerMarginCss
import root.LifecycleWrapper
import root.debugLog
import styled.css
import styled.styledDiv

class TodoDetailsComponent(props: TodoDetailsParentProps) :
    RComponent<TodoDetailsParentProps, TodoDetailsParentState>(props) {

    private val detailsViewProxy = TodoDetailsViewProxy(::updateState)
    private val lifecycleWrapper = LifecycleWrapper()
    private lateinit var controller: TodoDetailsController

    init {
        debugLog("init")
        state = TodoDetailsParentState(TodoDetailsView.Model("", false), true)
    }

    override fun componentDidMount() {
        debugLog("componentDidMount")
        lifecycleWrapper.start()
        controller = createController()
        controller.onViewCreated(
            detailsViewProxy,
            lifecycleWrapper.lifecycle,
            output = props.dependencies.output
        )
    }

    private fun createController(): TodoDetailsController {
        val dependencies = props.dependencies
        val todoDetailsControllerDependencies =
            object : TodoDetailsController.Dependencies, Dependencies by dependencies {
                override val lifecycle: Lifecycle = lifecycleWrapper.lifecycle
                override val itemId: String = dependencies.todoId
            }

        return when (dependencies.frameworkType) {
            FrameworkType.REAKTIVE -> TodoDetailsReaktiveController(todoDetailsControllerDependencies)
            FrameworkType.COROUTINES -> TodoDetailsCoroutinesController(todoDetailsControllerDependencies)
        }
    }

    private fun updateState(newModel: TodoDetailsView.Model) {
        setState { model = newModel }
    }

    override fun RBuilder.render() {
        debugLog("Details render")
        val model = state.model
        mDialog(
            open = state.open,
            fullScreen = true,
            transitionComponent = SlideUpTransitionComponent::class,
            onClose = { _, _ -> setState { open = false } }
        ) {
            debugLog(model.toString())
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
                                detailsViewProxy.dispatchEvent(
                                    TodoDetailsView.Event.TextChanged(it.targetInputValue)
                                )
                            }
                        )
                    }
                    styledDiv {
                        css (detailsButtonsCss)
                        mCheckboxWithLabel(
                            label = "complete",
                            checked = model.isDone,
                            onChange = { _, _ ->
                                detailsViewProxy.dispatchEvent(TodoDetailsView.Event.DoneClicked)
                            }
                        )
                        mIconButton(
                            iconName = "delete",
                            onClick = {
                                detailsViewProxy.dispatchEvent(TodoDetailsView.Event.DeleteClicked)
                            }
                        )
                    }


                }
            }
        }
        if (state.open.not()) {
            GlobalScope.launch {
                //hack for fade transition
                delay(ANIMATION_DURATION.toLong())
                props.dependencies.onClose()
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
        val output: (TodoDetailsController.Output) -> Unit
        val todoId: String
        val onClose: () -> Unit
    }

}

class TodoDetailsParentState(
    var model: TodoDetailsView.Model,
    var open: Boolean
) : RState

interface TodoDetailsParentProps : RProps {
    var dependencies: TodoDetailsComponent.Dependencies
}

fun RBuilder.todoDetails(
    dependencies: TodoDetailsComponent.Dependencies
) =
    child(TodoDetailsComponent::class) {
        attrs.dependencies = dependencies
    }