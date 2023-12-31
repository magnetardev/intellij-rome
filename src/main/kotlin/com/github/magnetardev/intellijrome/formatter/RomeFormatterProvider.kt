package com.github.magnetardev.intellijrome.formatter

import com.github.magnetardev.intellijrome.RomeBundle
import com.github.magnetardev.intellijrome.RomeUtils
import com.github.magnetardev.intellijrome.settings.RomeSettings
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService.Feature
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.psi.PsiFile
import com.intellij.util.SmartList
import org.jetbrains.annotations.NotNull
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutionException
import java.util.EnumSet

class RomeFormatterProvider: AsyncDocumentFormattingService() {
    override fun getFeatures(): MutableSet<Feature> = EnumSet.noneOf(Feature::class.java)

    override fun canFormat(file: PsiFile): Boolean = RomeUtils.isSupportedFileType(file.fileType)

    override fun getNotificationGroupId(): String = "Rome"

    override fun getName(): String = "Rome"

    override fun createFormattingTask(request: AsyncFormattingRequest): FormattingTask? {
        val ioFile = request.ioFile ?: return null
        val project = request.context.project
        val params = SmartList("format", "--stdin-file-path", ioFile.path)
        RomeUtils.attachConfigPath(params, project, thisLogger())

        try {
            val commandLine: GeneralCommandLine = GeneralCommandLine()
                .withExePath(RomeSettings.getInstance(project).executablePath)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                .withInput(ioFile)
                .withParameters(params)

            println(commandLine)

            val handler = OSProcessHandler(commandLine.withCharset(StandardCharsets.UTF_8))
            return object : FormattingTask {
                override fun run() {
                    handler.addProcessListener(object : CapturingProcessAdapter() {
                        override fun processTerminated(@NotNull event: ProcessEvent) {
                            val exitCode = event.exitCode
                            if (exitCode == 0) {
                                request.onTextReady(output.stdout)
                            } else {
                                request.onError(RomeBundle.message("rome.formatting.failure"), output.stderr)
                            }
                        }
                    })
                    handler.startNotify()
                }

                override fun cancel(): Boolean {
                    handler.destroyProcess()
                    return true
                }

                override fun isRunUnderProgress(): Boolean {
                    return true
                }
            }
        } catch (error: ExecutionException) {
            val message = error.message ?: ""
            request.onError(RomeBundle.message("rome.formatting.failure"), message)
            return null
        }
    }
}