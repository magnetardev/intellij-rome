package com.github.magnetardev.intellijrome.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "RomeSettings", storages = [(Storage("rome.xml"))])
class RomeSettings :
    SimplePersistentStateComponent<RomeSettingsState>(RomeSettingsState()) {
    var executablePath: String
        get() = state.executablePath ?: "rome"
        set(value) {
            state.executablePath = value
        }

    companion object {
        @JvmStatic
        fun getInstance(project: Project): RomeSettings = project.service()
    }
}