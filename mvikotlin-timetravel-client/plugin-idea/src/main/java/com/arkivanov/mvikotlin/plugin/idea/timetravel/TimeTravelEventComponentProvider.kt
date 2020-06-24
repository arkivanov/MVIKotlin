package com.arkivanov.mvikotlin.plugin.idea.timetravel

import org.jdesktop.swingx.renderer.CellContext
import org.jdesktop.swingx.renderer.ComponentProvider
import org.jdesktop.swingx.renderer.JRendererLabel
import java.awt.Font
import javax.swing.JLabel

internal class TimeTravelEventComponentProvider(
    private val font: Font,
    private val selectedEventIndex: Int
) : ComponentProvider<JLabel>() {

    override fun createRendererComponent(): JLabel = JRendererLabel()

    override fun configureState(cellContext: CellContext) {
        (rendererComponent as JLabel).horizontalAlignment = horizontalAlignment
    }

    override fun format(cellContext: CellContext) {
        val label = rendererComponent as JLabel
        label.text = cellContext.value as String
        label.font = font.deriveFont(if (selectedEventIndex == cellContext.row) Font.BOLD else Font.PLAIN)
    }
}
