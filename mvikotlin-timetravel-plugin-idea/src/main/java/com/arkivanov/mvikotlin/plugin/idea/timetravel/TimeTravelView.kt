package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.TimeTravelStateUpdate
import com.arkivanov.mvikotlin.utils.internal.ValueTextTreeBuilder
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

    private val toolbar = TimeTravelToolbar(listener, ::onDebug)
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
        toolbar.update(state.connectionStatus, state.mode, list.selectedValue?.type?.isDebuggable == true)
        list.updateUI()
        treeModel.setRoot(list.selectedValue?.value?.let(treeBuilder::build))
    }

    private fun onDebug() {
        list.selectedValue?.id?.also(listener::onDebug)
    }

    data class State(
        val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
        val selectedEventIndex: Int = -1,
        val mode: TimeTravelStateUpdate.Mode = TimeTravelStateUpdate.Mode.IDLE
    )

    interface Listener : TimeTravelToolbar.Listener {
        fun onDebug(eventId: Long)
    }
}
