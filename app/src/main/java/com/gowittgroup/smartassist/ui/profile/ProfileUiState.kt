package com.gowittgroup.smartassist.ui.profile

import com.gowittgroup.smartassist.core.State

data class ProfileUiState(
    val id: String = "",
    val photoUrl: String? = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val gender: String = ""
) : State