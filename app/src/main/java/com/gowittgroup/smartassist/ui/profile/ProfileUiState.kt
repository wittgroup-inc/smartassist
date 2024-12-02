package com.gowittgroup.smartassist.ui.profile

import com.gowittgroup.smartassist.core.State
import com.gowittgroup.smartassist.ui.NotificationState

data class ProfileUiState(
    val id: String = "",
    val photoUrl: String? = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val notificationState: NotificationState? = null,
    val isLoading: Boolean = false,
    val isProfileUpdateInProgress: Boolean = false
) : State