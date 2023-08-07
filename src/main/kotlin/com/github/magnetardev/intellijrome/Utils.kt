package com.github.magnetardev.intellijrome

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.util.SmartList
import java.io.File
import com.intellij.openapi.diagnostic.Logger

fun isSupportedFileType(fileType: FileType): Boolean {
    return when (fileType.defaultExtension) {
        "js", "mjs", "cjs", "jsx", "ts", "mts", "cts", "tsx" -> true
        else -> false
    }
}

fun attachConfigPath(params: SmartList<String>, project: Project, logger: Logger) {
    project.basePath?.let {
        try {
            val file = File(it)
            if (file.exists()) {
                params.add("--config-path")
                params.add(project.basePath)
            }
        } catch (error: Exception) {
            logger.error(error.message)
        }
    }
}