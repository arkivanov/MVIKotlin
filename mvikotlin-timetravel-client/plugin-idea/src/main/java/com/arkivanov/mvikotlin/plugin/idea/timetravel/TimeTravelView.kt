package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Event
import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView.Model
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.Value
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueTextTreeBuilder
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import org.jdesktop.swingx.renderer.DefaultListRenderer
import java.awt.BorderLayout
import javax.swing.DefaultListModel
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

internal class TimeTravelView(
    private val onConnect: () -> Boolean
) : BaseMviView<Model, Event>(), TimeTravelClientView {

    private val toolbar = TimeTravelToolbar(ToolbarListenerImpl())
    private val listModel = DefaultListModel<String>()

    private val list =
        JBList(listModel).apply {
            addListSelectionListener {
                dispatch(Event.EventSelected(index = selectedIndex))
            }
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

    override val renderer: ViewRenderer<Model>? =
        diff {
            diff(get = Model::events, set = ::renderEvents)
            diff(get = Model::currentEventIndex, set = ::renderCurrentEventIndex)
            diff(get = Model::buttons, set = toolbar::render)
            diff(get = Model::selectedEventIndex, set = ::renderSelectedEventIndex)
            diff(get = Model::selectedEventValue, set = ::renderSelectedEventValue)
        }

    override fun showError(text: String) {
        showErrorDialog(text)
    }

    private fun renderEvents(events: List<String>) {
        val selectedIndex = list.selectedIndex
        listModel.clear()
        events.forEach(listModel::addElement)
        list.selectedIndex = selectedIndex
        list.updateUI()
    }

    private fun renderCurrentEventIndex(selectedEventIndex: Int) {
        list.cellRenderer =
            DefaultListRenderer(
                TimeTravelEventComponentProvider(
                    font = list.font,
                    selectedEventIndex = selectedEventIndex
                )
            )
    }

    private fun renderSelectedEventIndex(index: Int) {
        list.selectedIndex = index
    }

    private fun renderSelectedEventValue(value: Value?) {
        treeModel.setRoot(value?.let(treeBuilder::build))
    }

    private inner class ToolbarListenerImpl : TimeTravelToolbar.Listener {
        override fun onConnect() {
            if (onConnect.invoke()) {
                dispatch(Event.ConnectClicked)
            }
        }

        override fun onDisconnect() {
            dispatch(Event.DisconnectClicked)
        }

        override fun onStartRecording() {
            dispatch(Event.StartRecordingClicked)
        }

        override fun onStopRecording() {
            dispatch(Event.StopRecordingClicked)
        }

        override fun onMoveToStart() {
            dispatch(Event.MoveToStartClicked)
        }

        override fun onStepBackward() {
            dispatch(Event.StepBackwardClicked)
        }

        override fun onStepForward() {
            dispatch(Event.StepForwardClicked)
        }

        override fun onMoveToEnd() {
            dispatch(Event.MoveToEndClicked)
        }

        override fun onCancel() {
            dispatch(Event.CancelClicked)
        }

        override fun onDebug() {
            dispatch(Event.DebugEventClicked)
        }
    }
}
