package com.gowittgroup.smartassist.ui.profile

import com.gowittgroup.smartassist.core.SideEffect

sealed class ProfileSideEffect : SideEffect {
    data object ProfileUpdateSuccess : ProfileSideEffect()
}