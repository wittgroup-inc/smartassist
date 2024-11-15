package com.gowittgroup.smartassist.ui.auth.signin

import com.gowittgroup.smartassist.core.State

data class SignInUiState(
    val email: String = "",
    val password: String = ""
): State