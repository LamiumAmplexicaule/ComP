package net.henbit.comp.actions

import cc.ekblad.toml.decode
import cc.ekblad.toml.model.TomlDocument
import cc.ekblad.toml.serialization.CollectionSyntax
import cc.ekblad.toml.serialization.InlineListMode
import cc.ekblad.toml.tomlMapper
import cc.ekblad.toml.tomlSerializer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import kotlin.io.path.exists
import io.ktor.utils.io.errors.*
import net.henbit.comp.Utils
import net.henbit.comp.dialog.RunNewCommandDialog
import kotlin.io.path.Path


class RunNewCommandAction : AnAction() {

    companion object {
        val LOGGER = Logger.getInstance(this::class.java)
        const val TAB_NAME = "ComP"
    }

    data class Cargo(val workspace: Workspace) {
        data class Workspace(val members: List<String>)
    }

    @Suppress("DuplicatedCode")
    override fun actionPerformed(event: AnActionEvent) {
        when (val project = event.project) {
            null -> LOGGER.error("Cannot execute command in local terminal. Project is NULL.")
            else -> {
                try {
                    val dialog = RunNewCommandDialog(project)
                    val ok = dialog.showAndGet()

                    if (!ok) return

                    val contestId = dialog.contestId

                    val widget = Utils.getShellTerminalWidget(TAB_NAME, project)

                    widget.executeCommand(
                        "cargo compete new $contestId"
                    )
                    val projectBasePath = project.basePath?.let { Path(it) }
                    val cargoFilePath = projectBasePath?.resolve("cargo.toml")
                    if (cargoFilePath != null) {
                        if (cargoFilePath.exists()) {
                            val mapper = tomlMapper { }
                            val cargo = mapper.decode<Cargo>(cargoFilePath)
                            val mutableMembers = cargo.workspace.members.toMutableList()
                            mutableMembers.add(contestId)
                            val newCargo = Cargo(Cargo.Workspace(mutableMembers.toList()))
                            val serializer = tomlSerializer {
                                inlineListMode(InlineListMode.MultiLine)
                                preferListSyntax(CollectionSyntax.Inline)
                            }
                            val toml = mapper.encode(newCargo) as TomlDocument
                            serializer.write(toml, cargoFilePath.toFile().outputStream())
                        }
                    }

                } catch (e: IOException) {
                    LOGGER.error("Cannot execute command in local terminal. Error:$e")
                }
            }
        }
    }
}