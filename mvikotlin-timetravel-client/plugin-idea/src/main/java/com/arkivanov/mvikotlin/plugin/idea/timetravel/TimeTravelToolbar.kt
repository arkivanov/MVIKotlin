package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient.Model.Buttons
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.JComponent

class TimeTravelToolbar(private val listener: Listener) {

    private val toolbar: ActionToolbar =
        ActionManager
            .getInstance()
            .createActionToolbar(ActionPlaces.COMMANDER_TOOLBAR, actionGroup(), true)

    private var buttons: Buttons =
        Buttons(
            isConnectEnabled = false,
            isDisconnectEnabled = false,
            isStartRecordingEnabled = false,
            isStopRecordingEnabled = false,
            isMoveToStartEnabled = false,
            isStepBackwardEnabled = false,
            isStepForwardEnabled = false,
            isMoveToEndEnabled = false,
            isCancelEnabled = false,
            isDebugEventEnabled = false,
            isExportEventsEnabled = false,
            isImportEventsEnabled = false
        )

    val component: JComponent get() = toolbar.component

    fun render(buttons: Buttons) {
        this.buttons = buttons
        toolbar.updateActionsImmediately()
    }

    private fun actionGroup(): DefaultActionGroup =
        DefaultActionGroup().apply {
            addAll(connectAction(), disconnectAction())
            addSeparator()
            addAll(
                startRecordingAction(),
                stopRecordingAction(),
                moveToStartAction(),
                stepBackwardAction(),
                stepForwardAction(),
                moveToEndAction(),
                cancelAction()
            )
            addSeparator()
            add(debugAction())
            addSeparator()
            add(exportAction())
            add(importAction())
        }

    private fun connectAction(): AnAction =
        anAction(
            text = "Connect",
            icon = AllIcons.Debugger.AttachToProcess,
            onUpdate = { it.presentation.isEnabled = buttons.isConnectEnabled },
            onAction = listener::onConnect
        )

    private fun disconnectAction(): AnAction =
        anAction(
            text = "Disconnect",
            icon = AllIcons.Debugger.Db_invalid_breakpoint,
            onUpdate = { it.presentation.isEnabled = buttons.isDisconnectEnabled },
            onAction = listener::onDisconnect
        )

    private fun startRecordingAction(): AnAction =
        anAction(
            text = "Start recording",
            icon = AllIcons.Debugger.Db_set_breakpoint,
            onUpdate = { it.presentation.isEnabled = buttons.isStartRecordingEnabled },
            onAction = listener::onStartRecording
        )

    private fun stopRecordingAction(): AnAction =
        anAction(
            text = "Stop recording",
            icon = AllIcons.Actions.Suspend,
            onUpdate = { it.presentation.isEnabled = buttons.isStopRecordingEnabled },
            onAction = listener::onStopRecording
        )

    private fun moveToStartAction(): AnAction =
        anAction(
            text = "Move to start",
            icon = AllIcons.Actions.Play_first,
            onUpdate = { it.presentation.isEnabled = buttons.isMoveToStartEnabled },
            onAction = listener::onMoveToStart
        )

    private fun stepBackwardAction(): AnAction =
        anAction(
            text = "Step backward",
            icon = AllIcons.Actions.Play_back,
            onUpdate = { it.presentation.isEnabled = buttons.isMoveToEndEnabled },
            onAction = listener::onStepBackward
        )

    private fun stepForwardAction(): AnAction =
        anAction(
            text = "Step forward",
            icon = AllIcons.Actions.Play_forward,
            onUpdate = { it.presentation.isEnabled = buttons.isStepBackwardEnabled },
            onAction = listener::onStepForward
        )

    private fun moveToEndAction(): AnAction =
        anAction(
            text = "Move to end",
            icon = AllIcons.Actions.Play_last,
            onUpdate = { it.presentation.isEnabled = buttons.isMoveToEndEnabled },
            onAction = listener::onMoveToEnd
        )

    private fun cancelAction(): AnAction =
        anAction(
            text = "Cancel",
            icon = AllIcons.Actions.Cancel,
            onUpdate = { it.presentation.isEnabled = buttons.isCancelEnabled },
            onAction = listener::onCancel
        )

    private fun debugAction(): AnAction =
        anAction(
            text = "Debug selected event",
            icon = AllIcons.Actions.StartDebugger,
            onUpdate = { it.presentation.isEnabled = buttons.isDebugEventEnabled },
            onAction = listener::onDebug
        )

    private fun exportAction(): AnAction =
        anAction(
            text = "Export events",
            icon = AllIcons.ToolbarDecorator.Export,
            onUpdate = { it.presentation.isEnabled = buttons.isExportEventsEnabled },
            onAction = listener::onExport
        )

    private fun importAction(): AnAction =
        anAction(
            text = "Import events",
            icon = AllIcons.ToolbarDecorator.Import,
            onUpdate = {
                it.presentation.isEnabled = buttons.isImportEventsEnabled
            },
            onAction = listener::onImport
        )


    interface Listener {
        fun onConnect()
        fun onDisconnect()
        fun onStartRecording()
        fun onStopRecording()
        fun onMoveToStart()
        fun onStepBackward()
        fun onStepForward()
        fun onMoveToEnd()
        fun onCancel()
        fun onDebug()
        fun onExport()
        fun onImport()
    }
}
