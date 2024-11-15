package com.gowittgroup.smartassist.ui.auth.signup

import com.gowittgroup.smartassist.core.State

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
): State