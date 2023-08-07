package com.github.magnetardev.intellijrome.settings

import com.github.magnetardev.intellijrome.RomeBundle
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel

class RomeSettingsConfigurable(internal val project: Project): BoundSearchableConfigurable(RomeBundle.message("rome.settings.name"), RomeBundle.message("rome.settings.name")) {
    override fun createPanel(): DialogPanel {
        val settings: RomeSettings = RomeSettings.getInstance(project)
        return panel {
            row {
                textFieldWithBrowseButton(RomeBundle.message("rome.path.label")) { fileChosen(it) }
                    .bindText(settings::executablePath)
            }
        }
    }

    fun fileChosen(file: VirtualFile): String {
        return file.path
    }
}