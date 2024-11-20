package com.gowittgroup.smartassist.ui.settingsscreen

import com.gowittgroup.smartassist.core.SideEffect

sealed class SettingsSideEffects : SideEffect {
    data class ShowToast(val message: String) : SettingsSideEffects()
    data object SignOut : SettingsSideEffects()
}