package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.utils.internal.ValueTextTreeBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import org.jdesktop.swingx.renderer.CellContext
import org.jdesktop.swingx.renderer.ComponentProvider
import org.jdesktop.swingx.renderer.DefaultListRenderer
import org.jdesktop.swingx.renderer.JRendererLabel
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

internal class TimeTravelView(
    private val listener: Listener
) {

    private var state = State()
    private val isConnected: Boolean get() = state.connectionStatus == ConnectionStatus.CONNECTED
    private val isDisconnected: Boolean get() = state.connectionStatus == ConnectionStatus.DISCONNECTED

    private val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.COMMANDER_TOOLBAR, toolbarActions(), true)
    private val listModel = DefaultListModel<TimeTravelEvent>()

    private val list =
        JBList(listModel).apply {
            cellRenderer = DefaultListRenderer(
                object : ComponentProvider<JLabel>() {
                    override fun createRendererComponent(): JLabel = JRendererLabel()

                    override fun configureState(cellContext: CellContext) {
                        (rendererComponent as JLabel).horizontalAlignment = horizontalAlignment
                    }

                    override fun format(cellContext: CellContext) {
                        val label = rendererComponent as JLabel
                        val event = cellContext.value as TimeTravelEvent
                        label.text = event.description
                        label.font = font.deriveFont(if (state.selectedEventIndex == cellContext.row) Font.BOLD else Font.PLAIN)
                    }
                }
            )

            addListSelectionListener { refreshUi() }
        }

    private val treeBuilder = ValueTextTreeBuilder(newNode = ::DefaultMutableTreeNode, addChild = DefaultMutableTreeNode::add)
    private val treeModel = DefaultTreeModel(null)
    private val tree = JTree(treeModel)

    val content: JComponent =
        JPanel(BorderLayout()).apply {
            add(toolbar.component, BorderLayout.NORTH)

            add(
                JBSplitter(false, 0.4F).apply {
                    firstComponent = JBScrollPane(list)
                    secondComponent = JBScrollPane(tree)
                },
                BorderLayout.CENTER
            )
        }

    init {
        refreshUi()
    }

    fun render(state: State, eventsUpdate: TimeTravelEventsUpdate? = null) {
        this.state = state
        eventsUpdate?.also(::onEventsUpdate)
        refreshUi()
    }

    private fun onEventsUpdate(eventsUpdate: TimeTravelEventsUpdate) {
        when (eventsUpdate) {
            is TimeTravelEventsUpdate.All -> {
                listModel.clear()
                addEvents(eventsUpdate.events)
            }

            is TimeTravelEventsUpdate.New -> addEvents(eventsUpdate.events)
        }.let {}
    }

    private fun addEvents(events: Iterable<TimeTravelEvent>) {
        events.forEach(listModel::addElement)
    }

    private fun refreshUi() {
        toolbar.updateActionsImmediately()
        list.updateUI()
        treeModel.setRoot(list.selectedValue?.value?.let(treeBuilder::build))
    }

    private fun toolbarActions(): DefaultActionGroup =
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
        }

    private fun connectAction(): AnAction =
        anAction(
            text = "Connect",
            icon = AllIcons.Debugger.AttachToProcess,
            onUpdate = { it.presentation.isEnabled = isDisconnected },
            onAction = { listener.onConnect() }
        )

    private fun disconnectAction(): AnAction =
        anAction(
            text = "Disconnect",
            icon = AllIcons.Debugger.Db_invalid_breakpoint,
            onUpdate = { it.presentation.isEnabled = !isDisconnected },
            onAction = { listener.onDisconnect() }
        )

    private fun startRecordingAction(): AnAction =
        anAction(
            text = "Start recording",
            icon = AllIcons.Debugger.Db_set_breakpoint,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isRecordingActionEnabled },
            onAction = { listener.onStartRecording() }
        )

    private val TimeTravelStateUpdate.Mode.isRecordingActionEnabled: Boolean
        get() =
            when (this) {
                TimeTravelStateUpdate.Mode.IDLE -> true
                TimeTravelStateUpdate.Mode.RECORDING,
                TimeTravelStateUpdate.Mode.STOPPED -> false
            }

    private fun stopRecordingAction(): AnAction =
        anAction(
            text = "Stop recording",
            icon = AllIcons.Actions.Suspend,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isStopActionEnabled },
            onAction = { listener.onStopRecording() }
        )

    private val TimeTravelStateUpdate.Mode.isStopActionEnabled: Boolean
        get() =
            when (this) {
                TimeTravelStateUpdate.Mode.IDLE -> false
                TimeTravelStateUpdate.Mode.RECORDING -> true
                TimeTravelStateUpdate.Mode.STOPPED -> false
            }

    private fun moveToStartAction(): AnAction =
        anAction(
            text = "Move to start",
            icon = AllIcons.Actions.Play_first,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isMovingActionEnabled },
            onAction = { listener.onMoveToStart() }
        )

    private fun stepBackwardAction(): AnAction =
        anAction(
            text = "Step backward",
            icon = AllIcons.Actions.Play_back,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isMovingActionEnabled },
            onAction = { listener.onStepBackward() }
        )

    private fun stepForwardAction(): AnAction =
        anAction(
            text = "Step forward ",
            icon = AllIcons.Actions.Play_forward,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isMovingActionEnabled },
            onAction = { listener.onStepForward() }
        )

    private fun moveToEndAction(): AnAction =
        anAction(
            text = "Move to end",
            icon = AllIcons.Actions.Play_last,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isMovingActionEnabled },
            onAction = { listener.onMoveToEnd() }
        )

    private val TimeTravelStateUpdate.Mode.isMovingActionEnabled: Boolean
        get() =
            when (this) {
                TimeTravelStateUpdate.Mode.IDLE,
                TimeTravelStateUpdate.Mode.RECORDING -> false
                TimeTravelStateUpdate.Mode.STOPPED -> true
            }

    private fun cancelAction(): AnAction =
        anAction(
            text = "Cancel",
            icon = AllIcons.Actions.Cancel,
            onUpdate = { it.presentation.isEnabled = isConnected && state.mode.isCancelActionEnabled },
            onAction = { listener.onCancel() }
        )

    private val TimeTravelStateUpdate.Mode.isCancelActionEnabled: Boolean
        get() =
            when (this) {
                TimeTravelStateUpdate.Mode.IDLE -> false
                TimeTravelStateUpdate.Mode.RECORDING -> true
                TimeTravelStateUpdate.Mode.STOPPED -> true
            }

    private fun debugAction(): AnAction =
        anAction(
            text = "Debug",
            icon = AllIcons.Actions.StartDebugger,
            onUpdate = { it.presentation.isEnabled = isConnected && (list.selectedValue?.type?.isDebuggable == true) },
            onAction = { list.selectedValue?.id?.also(listener::onDebug) }
        )

    data class State(
        val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
        val selectedEventIndex: Int = -1,
        val mode: TimeTravelStateUpdate.Mode = TimeTravelStateUpdate.Mode.IDLE
    )

    enum class ConnectionStatus {
        DISCONNECTED, CONNECTING, CONNECTED
    }

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

        fun onDebug(eventId: Long)
    }
}
