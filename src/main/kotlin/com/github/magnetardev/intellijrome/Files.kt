package com.github.magnetardev.intellijrome

import com.intellij.openapi.fileTypes.FileType

fun isSupportedFileType(fileType: FileType): Boolean {
    return when (fileType.defaultExtension) {
        "js", "mjs", "cjs", "jsx", "ts", "mts", "cts", "tsx" -> true
        else -> false
    }
}