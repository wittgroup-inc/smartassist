package com.gowittgroup.smartassist.ui.auth.signin

import com.gowittgroup.smartassist.core.SideEffect

sealed class SignInSideEffect : SideEffect {
    data object SignInSuccess : SignInSideEffect()
}