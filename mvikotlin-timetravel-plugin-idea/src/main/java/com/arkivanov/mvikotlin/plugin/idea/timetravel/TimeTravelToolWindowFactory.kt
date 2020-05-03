package com.arkivanov.mvikotlin.plugin.idea.timetravel

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class TimeTravelToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setIcon(AllIcons.Debugger.Db_muted_dep_line_breakpoint)

        toolWindow.contentManager.addContent(
            ContentFactory
                .SERVICE
                .getInstance()
                .createContent(TimeTravelToolWindow().content, "", false)
        )
    }
}
