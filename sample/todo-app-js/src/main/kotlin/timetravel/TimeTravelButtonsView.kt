package timetravel

import com.arkivanov.mvikotlin.timetravel.TimeTravelState
import com.ccfraser.muirwik.components.button.mIconButton
import react.RBuilder

fun RBuilder.timeTravelButtons(
    mode: TimeTravelState.Mode,
    onRecordClick: () -> Unit,
    onStopClick: () -> Unit,
    onMoveToStartClick: () -> Unit,
    onStepBackwardClick: () -> Unit,
    onStepForwardClick: () -> Unit,
    onMoveToEndClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    when (mode) {
        TimeTravelState.Mode.IDLE ->
            defaultButtons(
                onRecordClick = onRecordClick
            )
        TimeTravelState.Mode.RECORDING ->
            recordingButtons(
                onStopClick = onStopClick,
                onCancelClick = onCancelClick
            )
        TimeTravelState.Mode.STOPPED ->
            stoppedButtons(
                onMoveToStartClick = onMoveToStartClick,
                onStepForwardClick = onStepForwardClick,
                onStepBackwardClick = onStepBackwardClick,
                onMoveToEndClick = onMoveToEndClick,
                onCancelClick = onCancelClick
            )
    }.let {}
}

fun RBuilder.defaultButtons(onRecordClick: () -> Unit) {
    mIconButton(iconName = "fiber_manual_record", onClick = { onRecordClick() })
}

fun RBuilder.recordingButtons(onStopClick: () -> Unit, onCancelClick: () -> Unit) {
    mIconButton(iconName = "stop", onClick = { onStopClick() })
    mIconButton(iconName = "close", onClick = { onCancelClick() })
}

fun RBuilder.stoppedButtons(
    onMoveToStartClick: () -> Unit,
    onStepForwardClick: () -> Unit,
    onStepBackwardClick: () -> Unit,
    onMoveToEndClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    mIconButton(iconName = "skip_previous", onClick = { onMoveToStartClick() })
    mIconButton(iconName = "keyboard_arrow_left", onClick = { onStepBackwardClick() })
    mIconButton(iconName = "keyboard_arrow_right", onClick = { onStepForwardClick() })
    mIconButton(iconName = "skip_next", onClick = { onMoveToEndClick() })
    mIconButton(iconName = "close", onClick = { onCancelClick() })
}
