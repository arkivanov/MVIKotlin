package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import com.arkivanov.mvikotlin.timetravel.client.internal.client.TimeTravelClient.Model
import com.arkivanov.mvikotlin.timetravel.proto.internal.data.value.ValueNode
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
import javax.swing.tree.MutableTreeNode

internal class TimeTravelView(
    private val listener: Listener
) {

    private val toolbar = TimeTravelToolbar(listener)
    private val listModel = DefaultListModel<String>()

    private val list =
        JBList(listModel).apply {
            addListSelectionListener {
                listener.onEventSelected(index = selectedIndex)
            }
        }

    private val treeModel = DefaultTreeModel(null)
    private val tree = JTree(treeModel)

    val content: JComponent =
        JPanel(BorderLayout()).apply {
            add(toolbar.component, BorderLayout.NORTH)

            add(
                JBSplitter(false, SPLITTER_PROPORTION).apply {
                    firstComponent = JBScrollPane(list)
                    secondComponent = JBScrollPane(tree)
                },
                BorderLayout.CENTER
            )
        }

    private val renderer: ViewRenderer<Model> =
        diff {
            diff(get = Model::events, set = ::renderEvents)
            diff(get = Model::currentEventIndex, set = ::renderCurrentEventIndex)
            diff(get = Model::buttons, set = toolbar::render)
            diff(get = Model::selectedEventIndex, set = ::renderSelectedEventIndex)
            diff(get = Model::selectedEventValue, set = ::renderSelectedEventValue)
            diff(get = Model::errorText, set = ::renderError)
        }

    fun render(model: Model) {
        renderer.render(model)
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

    private fun renderSelectedEventValue(value: ValueNode?) {
        treeModel.setRoot(value?.toTreeNode())
    }

    private fun ValueNode.toTreeNode(): MutableTreeNode =
        DefaultMutableTreeNode(title).apply {
            children.forEach {
                add(it.toTreeNode())
            }
        }

    private fun renderError(text: String?) {
        if (text != null) {
            showErrorDialog(text = text)
        }
    }

    private companion object {
        private const val SPLITTER_PROPORTION = 0.4F
    }

    interface Listener : TimeTravelToolbar.Listener {
        fun onEventSelected(index: Int)
    }
}
