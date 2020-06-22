package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelevent.TimeTravelEvent
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetraveleventsupdate.TimeTravelEventsUpdate
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate
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
    private val listener: Listener
) {

    private val toolbar = TimeTravelToolbar(listener, ::onDebug)
    private val listModel = DefaultListModel<TimeTravelEvent>()

    private val list =
        JBList(listModel).apply {
            addListSelectionListener { refreshUi() }
        }

    private val treeBuilder = ValueTextTreeBuilder(newNode = ::DefaultMutableTreeNode, addChild = DefaultMutableTreeNode::add)
    private val treeModel = DefaultTreeModel(null)
    private val tree = JTree(treeModel)
    private var isUpdatingUi = false
    private var isUpdatingEvents = false
    private var isRefreshingUi = false

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
        updateUi(state)
        eventsUpdate?.also(::updateEvents)
        refreshUi()
    }

    private fun updateUi(state: State) {
        isUpdatingUi = true
        try {
            toolbar.setConnectionStatus(state.connectionStatus)
            toolbar.setMode(state.mode)

            list.cellRenderer =
                DefaultListRenderer(
                    TimeTravelEventComponentProvider(
                        font = list.font,
                        selectedEventIndex = state.selectedEventIndex
                    )
                )
        } finally {
            isUpdatingUi = false
        }
    }

    private fun updateEvents(eventsUpdate: TimeTravelEventsUpdate) {
        isUpdatingEvents = true
        try {
            when (eventsUpdate) {
                is TimeTravelEventsUpdate.All -> {
                    listModel.clear()
                    addEvents(eventsUpdate.events)
                }

                is TimeTravelEventsUpdate.New -> addEvents(eventsUpdate.events)
            }.let {}
        } finally {
            isUpdatingEvents = false
        }
    }

    private fun addEvents(events: Iterable<TimeTravelEvent>) {
        events.forEach(listModel::addElement)
    }

    private fun refreshUi() {
        if (isUpdatingUi || isUpdatingEvents || isRefreshingUi) {
            return
        }

        isRefreshingUi = true
        try {
            toolbar.setDebugEnabled(list.selectedValue?.type?.isDebuggable == true)
            list.updateUI()
            treeModel.setRoot(list.selectedValue?.value?.let(treeBuilder::build))
        } finally {
            isRefreshingUi = false
        }
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
