package com.gowittgroup.smartassist.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.gowittgroup.smartassist.ui.navigation.SmartAssistNavigationActions

@Composable
fun ProfileScreenRoute(
    expandedScreen: Boolean,
    openDrawer: () -> Unit,
    navigationActions: SmartAssistNavigationActions
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val uiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.sideEffects.collect { sideEffect ->
            when (sideEffect) {
                is ProfileSideEffect.ProfileUpdateSuccess -> {
                    navigationActions.navigateToHome(null, null)
                }
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        expandedScreen = expandedScreen,
        openDrawer = openDrawer,
        onFirstNameChange = profileViewModel::onFirstNameChange,
        onLastNameChange = profileViewModel::onLastNameChange,
        onDateOfBirthChange = profileViewModel::onDateOfBirthChange,
        onGenderChange = profileViewModel::onGenderChange,
        onSaveClick = profileViewModel::saveProfile,
        onCancel = profileViewModel::onCancel,
        onAvtarSelected = profileViewModel::onAvatarChange,
        onNotificationClose = profileViewModel::clearNotification
    )
}