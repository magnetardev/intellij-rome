package com.github.magnetardev.intellijrome.lsp

import com.github.magnetardev.intellijrome.attachConfigPath
import com.github.magnetardev.intellijrome.isSupportedFileType
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.util.SmartList

@Suppress("UnstableApiUsage")
class RomeLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (isSupportedFileType(file.fileType)) {
            serverStarter.ensureServerStarted(RomeLspServerDescriptor(project))
        }
    }
}

@Suppress("UnstableApiUsage")
private class RomeLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "Foo") {
    override fun isSupportedFile(file: VirtualFile) = isSupportedFileType(file.fileType)
    override fun createCommandLine(): GeneralCommandLine {
        val params = SmartList("lsp-proxy")
        attachConfigPath(params, project, thisLogger())

        return GeneralCommandLine()
            .withExePath("rome")
            .withParameters(params)
    }

    override val lspGoToDefinitionSupport = false
    override val lspCompletionSupport = null
}