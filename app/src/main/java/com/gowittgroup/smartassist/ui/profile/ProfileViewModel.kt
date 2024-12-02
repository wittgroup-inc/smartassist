package com.gowittgroup.smartassist.ui.profile

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.models.authentication.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModelWithStateIntentAndSideEffect<ProfileUiState, ProfileIntent, ProfileSideEffect>() {

    private var backupProfile = User()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            uiState.value.copy(isLoading = true).applyStateUpdate()
            val result =
                authenticationRepository.fetchUserProfile(authenticationRepository.currentUserId)
            when (result) {
                is Resource.Success -> {
                    backupProfile = result.data
                    updateStateFromUser(result.data)
                }

                is Resource.Error -> {
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishErrorState(
                        result.exception.message ?: "Something went wrong."
                    )
                }
            }
        }
    }

    private fun updateStateFromUser(user: User) {
        uiState.value.copy(
            isLoading = false,
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            dateOfBirth = user.dateOfBirth,
            gender = user.gender,
            photoUrl = user.photoUrl
        ).applyStateUpdate()
    }

    fun onCancel() {
        updateStateFromUser(backupProfile)
    }

    fun onFirstNameChange(newFirstName: String) {
        uiState.value.copy(firstName = newFirstName).applyStateUpdate()
    }

    fun onLastNameChange(newLastName: String) {
        uiState.value.copy(lastName = newLastName).applyStateUpdate()
    }

    fun onAvatarChange(avatarUrl: String) {
        uiState.value.copy(photoUrl = avatarUrl).applyStateUpdate()
    }

    fun onDateOfBirthChange(newDateOfBirth: String) {
        uiState.value.copy(dateOfBirth = newDateOfBirth).applyStateUpdate()
    }

    fun saveProfile() {
        uiState.value.copy(isProfileUpdateInProgress = true).applyStateUpdate()
        viewModelScope.launch {
            val result = authenticationRepository.updateProfile(
                user = User(
                    firstName = uiState.value.firstName,
                    lastName = uiState.value.lastName,
                    email = uiState.value.email,
                    dateOfBirth = uiState.value.dateOfBirth,
                    gender = uiState.value.gender,
                    photoUrl = uiState.value.photoUrl
                )
            )
            when (result) {
                is Resource.Success -> {
                    uiState.value.copy(isProfileUpdateInProgress = false).applyStateUpdate()
                    sendSideEffect(ProfileSideEffect.ProfileUpdateSuccess)
                }
                is Resource.Error -> {
                    uiState.value.copy(isProfileUpdateInProgress = false).applyStateUpdate()
                    publishErrorState(
                        result.exception.message ?: "Something went wrong"
                    )
                }
            }
        }
    }


    override fun getDefaultState(): ProfileUiState = ProfileUiState()

    override fun processIntent(intent: ProfileIntent) {

    }

    private fun publishErrorState(message: String) {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = message,
                type = NotificationType.ERROR,
                autoDismiss = true
            )

        ).applyStateUpdate()
    }

    fun clearNotification() {
        uiState.value.copy(
            notificationState = null
        ).applyStateUpdate()
    }
}
