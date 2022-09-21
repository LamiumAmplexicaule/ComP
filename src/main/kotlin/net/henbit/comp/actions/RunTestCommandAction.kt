package net.henbit.comp.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import io.ktor.utils.io.errors.*
import net.henbit.comp.Utils
import java.io.File


class RunTestCommandAction : AnAction() {

    companion object {
        val LOGGER = Logger.getInstance(this::class.java)
        const val TAB_NAME = "ComP"
    }

    @Suppress("DuplicatedCode")
    override fun actionPerformed(event: AnActionEvent) {
        when (val project = event.project) {
            null -> LOGGER.error("Cannot execute command in local terminal. Project is NULL.")
            else -> {
                try {
                    val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project)

                    fileEditorManager.selectedEditor?.let {
                        val current = it.file
                        if (current.path.isEmpty()) return
                        val split = current.path.split(File.separatorChar)
                        if (split.isEmpty() || split.size < 4) return
                        val contestName = split[split.lastIndex - 3]
                        if (!current.name.contains(".rs")) return
                        val taskName = current.name.substringBeforeLast('.')

                        val widget = Utils.getShellTerminalWidget(TAB_NAME, project)

                        widget.executeCommand(
                            "cargo compete test $taskName --package $contestName"
                        )
                    }

                } catch (e: IOException) {
                    LOGGER.error("Cannot execute command in local terminal. Error:$e")
                }
            }
        }
    }
}