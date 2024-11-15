package com.gowittgroup.smartassist.ui.auth.signup

import com.gowittgroup.smartassist.core.SideEffect

sealed class SignUpSideEffect: SideEffect {
    data object SignUpSuccess: SignUpSideEffect()
    data class SignUpFailed(val message: String): SignUpSideEffect()
}