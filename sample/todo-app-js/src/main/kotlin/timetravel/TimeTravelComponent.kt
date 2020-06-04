package timetravel

import com.arkivanov.mvikotlin.rx.Disposable
import com.arkivanov.mvikotlin.rx.observer
import com.arkivanov.mvikotlin.timetravel.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.arkivanov.mvikotlin.timetravel.controller.timeTravelController
import com.ccfraser.muirwik.components.MDrawerAnchor
import com.ccfraser.muirwik.components.mDrawer
import react.*
import root.App.TodoStyles.debugButtonsStyle
import root.App.TodoStyles.debugDrawerStyle
import styled.css
import styled.styledDiv

class TimeTravelComponent(prps: TimeTravelComponentProps) :
    RComponent<TimeTravelComponentProps, TimeTravelComponentState>(prps) {

    private var disposable: Disposable? = null

    init {
        state = TimeTravelComponentState(
            events = listOf(),
            mode = TimeTravelState.Mode.IDLE,
            selectedEventIndex = -1,
            showDialog = false,
            currentEvent = null
        )
    }

    override fun componentDidMount() {
        disposable = timeTravelController.states(observer { updateState(it) })
    }

    override fun RBuilder.render() {
        mDrawer(
            open = true,
            onClose = { setState { props.onClose() } },
            anchor = MDrawerAnchor.right
        ) {
            styledDiv {
                css(debugDrawerStyle)
                styledDiv {
                    timeTravelEventsView(
                        events = state.events,
                        selectedEventIndex = state.selectedEventIndex,
                        onDebugEventClick = { timeTravelController.debugEvent(eventId = it) },
                        onItemClick = { showDialog(it) }
                    )
                }
                styledDiv {
                    css(debugButtonsStyle)
                    timeTravelButtons(
                        mode = state.mode,
                        onRecordClick = { timeTravelController.startRecording() },
                        onStopClick = { timeTravelController.stopRecording() },
                        onMoveToStartClick = { timeTravelController.moveToStart() },
                        onStepBackwardClick = { timeTravelController.stepBackward() },
                        onStepForwardClick = { timeTravelController.stepForward() },
                        onMoveToEndClick = { timeTravelController.moveToEnd() },
                        onCancelClick = { timeTravelController.cancel() }
                    )
                }
            }
        }
    }

    private fun showDialog(event: TimeTravelEvent) {
        setState {
            showDialog = true
            currentEvent = event
        }
    }

    private fun hideDialog() {
        setState {
            showDialog = false
            currentEvent = null
        }
    }

    private fun updateState(it: TimeTravelState) {
        setState {
            events = it.events
            selectedEventIndex = it.selectedEventIndex
            mode = it.mode
        }
    }

    override fun componentWillUnmount() {
        disposable = null
    }


}

interface TimeTravelComponentProps : RProps {
    var onClose: () -> Unit
}

class TimeTravelComponentState(
    var events: List<TimeTravelEvent>,
    var mode: TimeTravelState.Mode,
    var selectedEventIndex: Int,
    var showDialog: Boolean,
    var currentEvent: TimeTravelEvent?
) : RState {
}

fun RBuilder.timeTravel(onClose: () -> Unit) = child(TimeTravelComponent::class) {
    attrs.onClose = onClose
}
