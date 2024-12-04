package com.gowittgroup.smartassist.ui.settingsscreen

import com.gowittgroup.smartassist.core.SideEffect

sealed class SettingsSideEffects : SideEffect {
    data object SignOut : SettingsSideEffects()
}