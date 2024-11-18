package com.gowittgroup.smartassist.ui.profile

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
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

    fun fetchProfile() {
        viewModelScope.launch {
            val result =
                authenticationRepository.fetchUserProfile(authenticationRepository.currentUserId)
            when (result) {
                is Resource.Success -> {
                    backupProfile = result.data
                    // Update UI state with fetched profile data
                    updateStateFromUser(result.data)
                }

                is Resource.Error -> sendSideEffect(
                    ProfileSideEffect.ShowError(
                        result.exception.message ?: "Something went wrong."
                    )
                )

                is Resource.Loading -> {
                    // Handle loading state
                }
            }
        }
    }

    private fun updateStateFromUser(user: User) {
        uiState.value.copy(
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
                is Resource.Success -> sendSideEffect(ProfileSideEffect.ProfileUpdateSuccess)
                is Resource.Error -> sendSideEffect(
                    ProfileSideEffect.ShowError(
                        result.exception.message ?: "Something went wrong"
                    )
                )

                is Resource.Loading -> {}
            }
        }
    }


    override fun getDefaultState(): ProfileUiState = ProfileUiState()

    override fun processIntent(intent: ProfileIntent) {
        // Handle other intents if needed
    }
}
