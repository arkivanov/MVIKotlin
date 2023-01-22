package com.arkivanov.mvikotlin.sample.reaktive.app

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.sample.coroutines.shared.TodoDispatchers
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainController
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Event
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Model
import com.arkivanov.mvikotlin.sample.coroutines.shared.main.MainView.Model.Item
import com.arkivanov.mvikotlin.sample.database.TodoDatabase
import com.arkivanov.mvikotlin.sample.database.TodoItem
import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.Overflow
import csstype.number
import csstype.pct
import kotlinx.coroutines.flow.Flow
import mui.material.AppBar
import mui.material.AppBarPosition
import mui.material.Box
import mui.material.Checkbox
import mui.material.FormControlVariant
import mui.material.Icon
import mui.material.IconButton
import mui.material.IconButtonEdge
import mui.material.ListItem
import mui.material.ListItemButton
import mui.material.ListItemIcon
import mui.material.ListItemText
import mui.material.SwitchBaseEdge
import mui.material.TextField
import mui.material.Toolbar
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.PropsWithSx
import mui.system.sx
import react.FC
import react.Props
import react.ReactNode
import react.create
import react.dom.onChange
import react.useEffectOnce
import react.useMemo
import react.useState

external interface MainProps : Props {
    var storeFactory: StoreFactory
    var database: TodoDatabase
    var dispatchers: TodoDispatchers
    var input: Flow<MainInput>
    var onItemSelected: (id: String) -> Unit
}

sealed interface MainInput {
    data class ItemChanged(val id: String, val data: TodoItem.Data) : MainInput
    data class ItemDeleted(val id: String) : MainInput
}

val MainComponent: FC<MainProps> = FC { props ->
    val lifecycle = useLifecycle()
    val instanceKeeper = useInstanceKeeper()

    val controller =
        useMemo {
            MainController(
                storeFactory = props.storeFactory,
                database = props.database,
                lifecycle = lifecycle,
                instanceKeeper = instanceKeeper,
                dispatchers = props.dispatchers,
                onItemSelected = props.onItemSelected,
            )
        }

    var model by useState(::Model)
    val view = useMemo { object : ViewProxy<Model, Event>(render = { model = it }), MainView {} }

    useCoroutineScope(context = props.dispatchers.main) {
        props.input.collect { input ->
            when (input) {
                is MainInput.ItemChanged -> controller.onItemChanged(id = input.id, data = input.data)
                is MainInput.ItemDeleted -> controller.onItemDeleted(id = input.id)
            }
        }
    }

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
                Typography {
                    sx {
                        flexGrow = number(1.0)
                    }

                    variant = TypographyVariant.h6

                    +"MVIKotlin"
                }
            }
        }

        ItemList {
            sx {
                flexGrow = number(1.0)
                overflowY = Overflow.scroll
            }

            items = model.items
            onItemClicked = { view.dispatch(Event.ItemClicked(id = it)) }
            onItemDoneClicked = { view.dispatch(Event.ItemDoneClicked(id = it)) }
            onItemDeleteClicked = { view.dispatch(Event.ItemDeleteClicked(id = it)) }
        }

        BottomBar {
            sx {
                flexGrow = number(0.0)
                display = Display.flex
                flexDirection = FlexDirection.row
                alignItems = AlignItems.center
                width = 100.pct
            }

            text = model.text
            onTextChanged = { view.dispatch(Event.TextChanged(text = it)) }
            onAddClicked = { view.dispatch(Event.AddClicked) }
        }
    }
}

private external interface ItemListProps : Props, PropsWithSx {
    var items: List<Item>
    var onItemClicked: (id: String) -> Unit
    var onItemDoneClicked: (id: String) -> Unit
    var onItemDeleteClicked: (id: String) -> Unit
}

private val ItemList = FC<ItemListProps> { props ->
    mui.material.List {
        sx {
            flexGrow = number(1.0)
            overflowY = Overflow.scroll
        }

        props.items.forEach { item ->
            ListItem {
                key = item.id

                secondaryAction =
                    IconButton.create {
                        edge = IconButtonEdge.end
                        onClick = { props.onItemDeleteClicked(item.id) }

                        Icon {
                            +"delete"
                        }
                    }

                disablePadding = true

                ListItemButton {
                    onClick = { props.onItemClicked(item.id) }

                    ListItemIcon {
                        Checkbox {
                            edge = SwitchBaseEdge.start
                            checked = item.isDone
                            onClick = {
                                it.stopPropagation()
                                props.onItemDoneClicked(item.id)
                            }
                        }
                    }

                    ListItemText {
                        primary = ReactNode(item.text)
                    }
                }
            }
        }
    }
}

private external interface BottomBarProps : Props, PropsWithSx {
    var text: String
    var onTextChanged: (String) -> Unit
    var onAddClicked: () -> Unit
}

private val BottomBar = FC<BottomBarProps> { props ->
    Box {
        sx = props.sx

        TextField {
            sx {
                flexGrow = number(1.0)
            }

            value = props.text
            variant = FormControlVariant.outlined
            onChange = { props.onTextChanged(it.target.asDynamic().value.unsafeCast<String>()) }
        }

        IconButton {
            sx {
                flexGrow = number(0.0)
            }

            onClick = { props.onAddClicked() }

            Icon {
                +"add"
            }
        }
    }
}
