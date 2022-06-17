package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.badoo.reaktive.disposable.Disposable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import java.util.function.Consumer
import javax.swing.AbstractButton
import javax.swing.Icon
import javax.swing.event.DocumentEvent
import javax.swing.text.JTextComponent

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
    text: String? = null,
    icon: Icon? = null,
    onUpdate: Consumer<AnActionEvent>? = null,
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

internal fun Disposable.attachTo(lifecycle: Lifecycle) {
    lifecycle.doOnDestroy(::dispose)
}

internal fun JTextComponent.addTextChangedListener(block: (String) -> Unit) {
    document.addDocumentListener(
        object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                block(text)
            }
        }
    )
}

internal fun AbstractButton.addSelectedChangedListener(block: (isSelected: Boolean) -> Unit) {
    addChangeListener { block(isSelected) }
}
