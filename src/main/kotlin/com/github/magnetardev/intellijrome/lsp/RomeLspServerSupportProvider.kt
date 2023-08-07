package com.github.magnetardev.intellijrome.lsp

import com.github.magnetardev.intellijrome.RomeUtils
import com.github.magnetardev.intellijrome.settings.RomeSettings
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
        if (RomeUtils.isSupportedFileType(file.fileType)) {
            serverStarter.ensureServerStarted(RomeLspServerDescriptor(project))
        }
    }
}

@Suppress("UnstableApiUsage")
private class RomeLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "Foo") {
    override fun isSupportedFile(file: VirtualFile) = RomeUtils.isSupportedFileType(file.fileType)
    override fun createCommandLine(): GeneralCommandLine {
        val params = SmartList("lsp-proxy")
        RomeUtils.attachConfigPath(params, project, thisLogger())

        return GeneralCommandLine()
            .withExePath(RomeSettings.getInstance(project).executablePath)
            .withParameters(params)
    }

    override val lspGoToDefinitionSupport = false
    override val lspCompletionSupport = null
}