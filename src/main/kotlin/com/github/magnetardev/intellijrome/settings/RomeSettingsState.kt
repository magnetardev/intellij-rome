package com.github.magnetardev.intellijrome.settings
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.Service
import org.jetbrains.annotations.ApiStatus

@Service
@ApiStatus.Internal
class RomeSettingsState: BaseState() {
    var executablePath by string()
}