package com.github.magnetardev.intellijrome.formatter

import com.github.magnetardev.intellijrome.MyBundle
import com.github.magnetardev.intellijrome.isSupportedFileType
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessAdapter
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessEvent
import com.intellij.formatting.service.AsyncDocumentFormattingService
import com.intellij.formatting.service.AsyncFormattingRequest
import com.intellij.formatting.service.FormattingService.Feature
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.NotNull
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ExecutionException

class RomeFormatterProvider: AsyncDocumentFormattingService() {
    override fun getFeatures(): MutableSet<Feature> = EnumSet.noneOf(Feature::class.java)

    override fun canFormat(file: PsiFile): Boolean = isSupportedFileType(file.fileType)

    override fun getNotificationGroupId(): String = "Rome"

    override fun getName(): String = "Rome"

    override fun createFormattingTask(request: AsyncFormattingRequest): FormattingTask? {
        val ioFile = request.ioFile ?: return null

        try {
            val commandLine: GeneralCommandLine = GeneralCommandLine("rome", "format", "--stdin-file-path=" + ioFile.path)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE)
                .withInput(ioFile)

            val handler = OSProcessHandler(commandLine.withCharset(StandardCharsets.UTF_8))
            return object : FormattingTask {
                override fun run() {
                    handler.addProcessListener(object : CapturingProcessAdapter() {
                        override fun processTerminated(@NotNull event: ProcessEvent) {
                            val exitCode = event.exitCode
                            if (exitCode == 0) {
                                request.onTextReady(output.stdout)
                            } else {
                                request.onError(MyBundle.message("formatting.failure"), output.stderr)
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
            request.onError(MyBundle.message("formatting.failure"), message)
            return null
        }
    }
}