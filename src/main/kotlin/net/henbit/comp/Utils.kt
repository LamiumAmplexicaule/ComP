package net.henbit.comp

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.terminal.ShellTerminalWidget
import org.jetbrains.plugins.terminal.TerminalToolWindowFactory
import org.jetbrains.plugins.terminal.TerminalView

class Utils {

    companion object {
        fun getShellTerminalWidget(tabName: String, project: Project): ShellTerminalWidget {
            val terminalView = TerminalView.getInstance(project)
            val window = ToolWindowManager.getInstance(project)
                .getToolWindow(TerminalToolWindowFactory.TOOL_WINDOW_ID)
            val contentManager = window?.contentManager

            val widget = when (val content = contentManager?.findContent(tabName)) {
                null -> terminalView.createLocalShellWidget(project.basePath, tabName)
                else -> TerminalView.getWidgetByContent(content) as ShellTerminalWidget
            }
            widget.terminalTitle.change {
                this.userDefinedTitle = tabName
            }
            return widget
        }
    }

}