package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class TimeTravelToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        initIfNeeded()

        toolWindow.icon = AllIcons.Debugger.Db_muted_dep_line_breakpoint

        toolWindow.contentManager.addContent(
            ContentFactory
                .SERVICE
                .getInstance()
                .createContent(
                    TimeTravelToolWindow(project).getContent(TimeTravelToolWindowListener.getLifecycle()),
                    "",
                    false
                )
        )
    }

    companion object {
        const val TOOL_WINDOW_ID: String = "MVIKotlin Time Travel"
        private var isInitialized = false

        fun initIfNeeded() {
            if (!isInitialized) {
                isInitialized = true

                overrideSchedulers(main = ::SwingMainScheduler)
                setMainThreadId(Thread.currentThread().id)
            }
        }
    }
}
