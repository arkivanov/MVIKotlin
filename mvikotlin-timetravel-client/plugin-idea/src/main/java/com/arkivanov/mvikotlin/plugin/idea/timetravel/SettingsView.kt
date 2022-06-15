package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings
import com.arkivanov.mvikotlin.timetravel.client.internal.settings.TimeTravelSettings.Model
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities

internal class SettingsView(
    private val settings: TimeTravelSettings,
) {

    private var dialogWrapper: SettingsDialogWrapper? = null

    fun render(model: Model) {
        val editing = model.editing

        if (editing != null) {
            if (dialogWrapper == null) {
                dialogWrapper = SettingsDialogWrapper(settings)
                SwingUtilities.invokeLater { dialogWrapper?.show() }
            }

            dialogWrapper?.render(editing)
        } else {
            dialogWrapper?.takeIf { it.isVisible }?.close(DialogWrapper.CLOSE_EXIT_CODE)
            dialogWrapper = null
        }
    }

    private class SettingsDialogWrapper(
        private val settings: TimeTravelSettings,
    ) : DialogWrapper(true) {
        private val hostText = JTextField()
        private val portText = JTextField()
        private val connectViaAdbCheckBox = JCheckBox("Connect via ADB")

        init {
            title = "Settings"
            init()

            window.addWindowListener(
                object : WindowAdapter() {
                    override fun windowClosed(event: WindowEvent) {
                        settings.onCancelClicked()
                    }
                }
            )

            hostText.addTextChangedListener(settings::onHostChanged)
            portText.addTextChangedListener(settings::onPortChanged)
            connectViaAdbCheckBox.addSelectedChangedListener(settings::onConnectViaAdbChanged)
        }

        override fun createCenterPanel(): JComponent =
            JPanel(GridBagLayout()).apply {
                add(
                    JLabel("Host:"),
                    GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL
                        gridx = 0
                        gridy = 0
                    }
                )

                add(
                    hostText,
                    GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL
                        gridx = 1
                        gridy = 0
                        weightx = 1.0
                    }
                )

                add(
                    JLabel("Port:"),
                    GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL
                        gridx = 0
                        gridy = 1
                    }
                )

                add(
                    portText,
                    GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL
                        gridx = 1
                        gridy = 1
                        weightx = 1.0
                    }
                )

                add(
                    connectViaAdbCheckBox,
                    GridBagConstraints().apply {
                        fill = GridBagConstraints.HORIZONTAL
                        gridx = 0
                        gridy = 2
                        gridwidth = 2
                    }
                )
            }

        fun render(model: Model.Editing) {
            hostText.takeUnless { it.text == model.host }?.text = model.host
            portText.takeUnless { it.text == model.port }?.text = model.port
            connectViaAdbCheckBox.takeUnless { it.isSelected == model.connectViaAdb }?.isSelected = model.connectViaAdb
        }

        override fun doOKAction() {
            super.doOKAction()

            settings.onSaveClicked()
        }

        override fun doCancelAction() {
            super.doCancelAction()

            settings.onCancelClicked()
        }
    }
}
