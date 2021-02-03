package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import java.util.function.Consumer
import javax.swing.Icon

internal fun logI(text: String) {
    Logger.getInstance("MVIKotlin").info(text)
}

internal fun logE(text: String, e: Throwable? = null) {
    Logger.getInstance("MVIKotlin").error(text, e)
}

internal fun showErrorDialog(text: String) {
    Messages.showErrorDialog(text, "MVIKotlin")
}

internal fun showInfoDialog(text: String) {
    Messages.showInfoMessage(text, "MVIKotlin")
}

internal fun anAction(
    text: String?,
    icon: Icon?,
    onUpdate: Consumer<AnActionEvent>?,
    onAction: Runnable
): AnAction =
    object : AnAction() {
        init {
            templatePresentation.text = text
            templatePresentation.icon = icon
        }

        override fun actionPerformed(event: AnActionEvent) {
            onAction.run()
        }

        override fun update(event: AnActionEvent) {
            onUpdate?.accept(event)
        }
    }
