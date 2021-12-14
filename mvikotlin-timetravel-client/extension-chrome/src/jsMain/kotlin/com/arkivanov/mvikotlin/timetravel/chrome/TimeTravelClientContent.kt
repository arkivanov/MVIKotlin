package com.arkivanov.mvikotlin.timetravel.chrome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
import kotlinx.browser.window
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.backgroundColor
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.cursor
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.flexFlow
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.overflow
import org.jetbrains.compose.web.css.overflowY
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.css.whiteSpace
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

@Composable
fun TimeTravelClientContent(client: TimeTravelClient) {
    val model by client.models.subscribeAsState()

    Div(
        attrs = {
            style {
                width(100.percent)
                height(100.vh)
                display(DisplayStyle.Flex)
                flexFlow(FlexDirection.Column, FlexWrap.Nowrap)
            }
        }
    ) {
        ButtonBar(
            buttons = model.buttons,
            onConnect = client::onConnectClicked,
            onDisconnect = client::onDisconnectClicked,
            onStartRecording = client::onStartRecordingClicked,
            onStopRecording = client::onStopRecordingClicked,
            onMoveToStart = client::onMoveToStartClicked,
            onStepBackward = client::onStepBackwardClicked,
            onStepForward = client::onStepForwardClicked,
            onMoveToEnd = client::onMoveToEndClicked,
            onCancel = client::onCancelClicked,
            onDebug = client::onDebugEventClicked,
            attrs = {
                style {
                    flex("0 1 auto")
                }
            }
        )

        Events(
            events = model.events,
            currentEventIndex = model.currentEventIndex,
            selectedEventIndex = model.selectedEventIndex,
            selectedEventValue = model.selectedEventValue,
            attrs = {
                style {
                    flex("1 1 auto")
                    overflowY("hidden")
                }
            },
            onClick = client::onEventSelected,
        )
    }

    model.errorText?.also {
        DisposableEffect(it) {
            window.alert(it)
            client.onDismissErrorClicked()
            onDispose {}
        }
    }
}

@Composable
private fun ButtonBar(
    buttons: TimeTravelClient.Model.Buttons,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onMoveToStart: () -> Unit,
    onStepBackward: () -> Unit,
    onStepForward: () -> Unit,
    onMoveToEnd: () -> Unit,
    onCancel: () -> Unit,
    onDebug: () -> Unit,
    attrs: AttrBuilderContext<*>? = null,
) {
    Div(
        attrs = {
            style {
                width(100.percent)
                display(DisplayStyle.Flex)
                flexFlow(FlexDirection.Row, FlexWrap.Nowrap)
                alignItems(AlignItems.Center)
            }

            add(attrs)
        }
    ) {
        ConnectionButtons(
            buttons = buttons,
            onConnect = onConnect,
            onDisconnect = onDisconnect,
        )

        ControlButtons(
            buttons = buttons,
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording,
            onMoveToStart = onMoveToStart,
            onStepBackward = onStepBackward,
            onStepForward = onStepForward,
            onMoveToEnd = onMoveToEnd,
            onCancel = onCancel,
            onDebug = onDebug,
        )
    }
}

@Composable
private fun ControlButtons(
    buttons: TimeTravelClient.Model.Buttons,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onMoveToStart: () -> Unit,
    onStepBackward: () -> Unit,
    onStepForward: () -> Unit,
    onMoveToEnd: () -> Unit,
    onCancel: () -> Unit,
    onDebug: () -> Unit
) {
    ImageButton(
        iconName = "fiber_manual_record",
        title = "Start recording",
        isEnabled = buttons.isStartRecordingEnabled,
        onClick = onStartRecording,
    )

    ImageButton(
        iconName = "stop",
        title = "Stop recording",
        isEnabled = buttons.isStopRecordingEnabled,
        onClick = onStopRecording,
    )

    ImageButton(
        iconName = "skip_previous",
        title = "Move to start",
        isEnabled = buttons.isMoveToStartEnabled,
        onClick = onMoveToStart,
    )

    ImageButton(
        iconName = "chevron_left",
        title = "Step backward",
        isEnabled = buttons.isStepBackwardEnabled,
        onClick = onStepBackward,
    )

    ImageButton(
        iconName = "chevron_right",
        title = "Step forward",
        isEnabled = buttons.isStepBackwardEnabled,
        onClick = onStepForward,
    )

    ImageButton(
        iconName = "skip_next",
        title = "Move to end",
        isEnabled = buttons.isMoveToEndEnabled,
        onClick = onMoveToEnd,
    )

    ImageButton(
        iconName = "close",
        title = "Cancel",
        isEnabled = buttons.isCancelEnabled,
        onClick = onCancel,
    )

    ImageButton(
        iconName = "bug_report",
        title = "Debug the selected event",
        isEnabled = buttons.isDebugEventEnabled,
        onClick = onDebug,
    )
}

@Composable
private fun ConnectionButtons(
    buttons: TimeTravelClient.Model.Buttons,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    ImageButton(
        iconName = "phonelink",
        title = "Connect to application",
        isEnabled = buttons.isConnectEnabled,
        onClick = onConnect,
    )

    ImageButton(
        iconName = "phonelink_off",
        title = "Disconnect from application",
        isEnabled = buttons.isDisconnectEnabled,
        onClick = onDisconnect,
    )
}

@Composable
private fun Events(
    events: List<String>,
    currentEventIndex: Int,
    selectedEventIndex: Int,
    selectedEventValue: ValueNode?,
    attrs: AttrBuilderContext<*>? = null,
    onClick: (Int) -> Unit
) {
    Div(
        attrs = {
            style {
                width(100.percent)
                display(DisplayStyle.Flex)
                flexFlow(FlexDirection.Row, FlexWrap.Nowrap)
            }

            add(attrs)
        }
    ) {
        EventList(
            events = events,
            currentEventIndex = currentEventIndex,
            selectedEventIndex = selectedEventIndex,
            attrs = {
                style {
                    flex("2 1 0")
                    overflow("scroll")
                }
            },
            onClick = onClick
        )

        EventDetails(
            value = selectedEventValue,
            attrs = {
                style {
                    flex("3 1 0")
                    overflow("scroll")
                }
            }
        )
    }
}

@Composable
private fun EventList(
    events: List<String>,
    currentEventIndex: Int,
    selectedEventIndex: Int,
    attrs: AttrBuilderContext<*>? = null,
    onClick: (Int) -> Unit,
) {
    Ul(
        attrs = {
            style {
                width(100.percent)
                margin(0.px)
            }

            add(attrs)
        }
    ) {
        events.forEachIndexed { index, item ->
            Event(
                text = item,
                isActive = index <= currentEventIndex,
                isSelected = index == selectedEventIndex,
                onClick = { onClick(index) },
            )
        }
    }
}

@Composable
private fun Event(
    text: String,
    isActive: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Li(
        attrs = {
            style {
                width(100.percent)
                padding(0.px, 16.px, 0.px, 16.px)
                cursor("pointer")

                if (isSelected) {
                    backgroundColor(Color("LightGray"))
                }
            }

            onClick { onClick() }
        }
    ) {
        Div(
            attrs = {
                style {
                    height(24.px)
                    whiteSpace("nowrap")
                    display(DisplayStyle.Flex)
                    alignItems(AlignItems.Center)
                    color(Color(if (isActive) "black" else "gray"))
                }
            }
        ) {
            Text(value = text)
        }
    }
}

@Composable
private fun EventDetails(value: ValueNode?, attrs: AttrBuilderContext<*>? = null) {
    Div(
        attrs = {
            style {
                width(100.percent)
                padding(16.px, 16.px, 16.px, 16.px)
                whiteSpace("pre")
                fontFamily("monospace")
            }

            add(attrs)
        }
    ) {
        Text(value = value?.title ?: "")
    }
}
