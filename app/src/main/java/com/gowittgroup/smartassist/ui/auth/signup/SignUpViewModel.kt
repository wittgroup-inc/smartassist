package com.gowittgroup.smartassist.ui.auth.signup

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.util.isEmailValid
import com.gowittgroup.smartassist.util.isPasswordStrong
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import com.gowittgroup.smartassistlib.models.authentication.SignUpModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : BaseViewModelWithStateIntentAndSideEffect<SignUpUiState, SignUpIntent, SignUpSideEffect>() {

    override fun getDefaultState(): SignUpUiState = SignUpUiState()

    override fun processIntent(intent: SignUpIntent) {
    }

    fun updateEmail(newEmail: String) {
        uiState.value.copy(
            email = newEmail,
            emailError = if (isEmailValid(newEmail)) null else "Invalid email format"
        ).applyStateUpdate()
        updateFormValidity()
    }

    fun updatePassword(newPassword: String) {
        uiState.value.copy(
            password = newPassword,
            passwordError = if (isPasswordStrong(newPassword)) null else "Password is too weak, Password must be at least 8 characters, with upper and lowercase letters, a number, and a special character."
        ).applyStateUpdate()
        updateFormValidity()
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        uiState.value.copy(
            confirmPassword = newConfirmPassword,
            confirmPasswordError = if (newConfirmPassword == uiState.value.password) null else "Passwords do not match"
        ).applyStateUpdate()
        updateFormValidity()
    }

    fun updateFirstName(newFirstName: String) {
        uiState.value.copy(
            firstName = newFirstName,
            firstNameError = if (newFirstName.isNotBlank()) null else "First Name is required"
        ).applyStateUpdate()
        updateFormValidity()
    }

    fun updateLastName(newLastName: String) {
        uiState.value.copy(
            lastName = newLastName,
            lastNameError = if (newLastName.isNotBlank()) null else "Last Name is required"
        ).applyStateUpdate()
        updateFormValidity()
    }

    fun updateTermsChecked(isChecked: Boolean) {
        uiState.value.copy(isTermsAccepted = isChecked)
            .applyStateUpdate()
        updateFormValidity()
    }

    private fun updateFormValidity() {
        val isFormValid = uiState.value.run {
            (email.isNotBlank() && emailError.isNullOrBlank()) &&
                    (password.isNotBlank() && passwordError.isNullOrBlank()) &&
                    (confirmPassword.isNotBlank() && confirmPasswordError.isNullOrBlank()) &&
                    (firstName.isNotBlank() && firstNameError.isNullOrBlank()) &&
                    (lastName.isNotBlank() && lastNameError.isNullOrBlank()) &&
                    isTermsAccepted
        }

        uiState.value.copy(isSignUpEnabled = isFormValid).applyStateUpdate()
    }

    fun onSignUpClick() {
        viewModelScope.launch {

            if (uiState.value.password != uiState.value.confirmPassword) {
                throw Exception("Passwords do not match")
            }

            uiState.value.copy(isLoading = true).applyStateUpdate()

            val res = authRepository.signUp(
                SignUpModel(
                    email = uiState.value.email,
                    password = uiState.value.password,
                    firstName = uiState.value.firstName,
                    lastName = uiState.value.lastName
                )
            )

            when (res) {
                is Resource.Success -> {
                    resetForm()
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishRegistrationSuccessState()
                }

                is Resource.Error -> {
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishErrorState(res.exception.message ?: "Something went wrong.")
                }
            }
        }
    }

    private fun publishErrorState(message: String) {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = message,
                type = NotificationType.ERROR
            )

        ).applyStateUpdate()
    }

    private fun publishRegistrationSuccessState() {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = "You got registered with us successfully, please check your email and verify.",
                type = NotificationType.SUCCESS,
                autoDismiss = false
            )
        ).applyStateUpdate()
    }

    fun onNotificationClose() {
        uiState.value.copy(
            notificationState = null
        ).applyStateUpdate()
    }

    fun closeNotificationAndNavigateToLogin() {
        onNotificationClose()
        sendSideEffect(SignUpSideEffect.NavigateToLogin)
    }

    private fun resetForm() {
        updateState(SignUpUiState())
    }
}

