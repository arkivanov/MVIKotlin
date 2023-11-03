@file:Suppress("MatchingDeclarationName")

package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsController
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.details.DetailsView.Model
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.Checkbox
import mui.material.Icon
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.IconButtonEdge
import mui.material.Input
import mui.material.Size
import mui.material.Stack
import mui.material.StackDirection
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.useEffectOnce
import react.useMemo
import react.useState
import web.cssom.AlignItems
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.number
import web.cssom.pct
import web.cssom.px

external interface DetailsProps : Props {
    var storeFactory: StoreFactory
    var database: TodoDatabase
    var dispatchers: TodoDispatchers
    var itemId: String
    var onFinished: () -> Unit
    var onItemChanged: (id: String, data: TodoItem.Data) -> Unit
    var onItemDeleted: (id: String) -> Unit
}

val DetailsComponent = FC<DetailsProps> { props ->
    val lifecycle = useLifecycle()

    val controller =
        useMemo {
            DetailsController(
                storeFactory = props.storeFactory,
                database = props.database,
                lifecycle = lifecycle,
                dispatchers = props.dispatchers,
                itemId = props.itemId,
                onItemChanged = props.onItemChanged,
                onItemDeleted = props.onItemDeleted,
            )
        }

    var model by useState(::Model)
    val view = useMemo { object : ViewProxy<Model, Event>(render = { model = it }), DetailsView {} }

    useEffectOnce {
        controller.onViewCreated(view, lifecycle)
    }

    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            width = 100.pct
            height = 100.pct
        }

        AppBar {
            sx {
                flexGrow = number(0.0)
                width = 100.pct
            }

            position = AppBarPosition.static

            Toolbar {
                IconButton {
                    sx {
                        marginRight = 4.px
                    }

                    size = Size.large
                    edge = IconButtonEdge.start
                    color = IconButtonColor.inherit
                    onClick = { props.onFinished() }

                    Icon {
                        +"arrow_back"
                    }
                }

                Typography {
                    sx {
                        flexGrow = number(1.0)
                    }

                    variant = TypographyVariant.h6

                    +"Details"
                }

                IconButton {
                    color = IconButtonColor.inherit
                    onClick = { view.dispatch(Event.DeleteClicked) }

                    Icon {
                        +"delete"
                    }
                }
            }
        }

        Input {
            sx {
                flexGrow = number(1.0)
                display = Display.flex
                flexDirection = FlexDirection.column
                padding = 8.px
            }

            value = model.text
            multiline = true
            fullWidth = true
            onChange = { view.dispatch(Event.TextChanged(text = it.target.asDynamic().value.unsafeCast<String>())) }
        }

        Stack {
            sx {
                flexGrow = number(0.0)
                alignItems = AlignItems.center
                justifyContent = JustifyContent.center
            }

            direction = responsive(StackDirection.row)

            Checkbox {
                checked = model.isDone
                onClick = { view.dispatch(Event.DoneClicked) }
            }

            Typography {
                +"Done"
            }
        }
    }
}
