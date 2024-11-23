package com.gowittgroup.smartassist.ui.auth.signin

import com.gowittgroup.smartassist.core.State

data class SignInUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val emailError: String? = "",
    val password: String = "",
    val passwordError: String? = "",
    val isSignInEnabled: Boolean = false,
    val isRestPasswordEnabled: Boolean = false
) : State