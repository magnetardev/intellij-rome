package com.github.magnetardev.intellijrome.lsp

import com.github.magnetardev.intellijrome.isSupportedFileType
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor

class RomeLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        if (isSupportedFileType(file.fileType)) {
            serverStarter.ensureServerStarted(RomeLspServerDescriptor(project))
        }
    }
}

private class RomeLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "Foo") {
    override fun isSupportedFile(file: VirtualFile) = isSupportedFileType(file.fileType)
    override fun createCommandLine() = GeneralCommandLine("rome", "lsp-proxy")
}