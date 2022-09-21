package net.henbit.comp.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import io.ktor.utils.io.errors.*
import net.henbit.comp.Utils


class RunInitCommandAction : AnAction() {

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
                    val widget = Utils.getShellTerminalWidget(TAB_NAME, project)

                    widget.executeCommand(
                        "cargo compete init"
                    )

                } catch (e: IOException) {
                    LOGGER.error("Cannot execute command in local terminal. Error:$e")
                }
            }
        }
    }
}