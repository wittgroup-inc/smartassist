package com.gowittgroup.smartassist.ui.auth.signin

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassist.ui.NotificationState
import com.gowittgroup.smartassist.ui.components.NotificationType
import com.gowittgroup.smartassist.util.isEmailValid
import com.gowittgroup.smartassistlib.domain.models.Resource
import com.gowittgroup.smartassistlib.domain.repositories.authentication.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : BaseViewModelWithStateIntentAndSideEffect<SignInUiState, SignInIntent, SignInSideEffect>() {

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
            passwordError = if (newPassword.isNotBlank()) null else "Enter password"
        ).applyStateUpdate()
        updateFormValidity()
    }

    private fun updateFormValidity() {
        val isSignInFormValid = uiState.value.run {
            (email.isNotBlank() && emailError.isNullOrBlank()) && (password.isNotBlank() && passwordError.isNullOrBlank())
        }
        uiState.value.copy(isSignInEnabled = isSignInFormValid).applyStateUpdate()

        val isRestPasswordFormValid = uiState.value.run {
            email.isNotBlank() && emailError.isNullOrBlank()
        }

        uiState.value.copy(isRestPasswordEnabled = isRestPasswordFormValid).applyStateUpdate()
    }

    fun onSignInClick() {
        viewModelScope.launch {
            uiState.value.copy(isLoading = true).applyStateUpdate()
            when (val res = authRepository.signIn(uiState.value.email, uiState.value.password)) {
                is Resource.Success -> {
                    resetSignInSate()
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    sendSideEffect(SignInSideEffect.SignInSuccess)
                }

                is Resource.Error -> {
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishErrorState(res.exception.message ?: "Something went wrong.")
                }
            }
        }
    }

    override fun getDefaultState(): SignInUiState = SignInUiState()

    override fun processIntent(intent: SignInIntent) {
        TODO("Not yet implemented")
    }

    fun onResetPasswordClick() {
        viewModelScope.launch {
            uiState.value.copy(isLoading = true).applyStateUpdate()
            when (val res = authRepository.resetPassword(email = uiState.value.email)) {
                is Resource.Success -> {
                    resetSignInSate()
                    uiState.value.copy(isLoading = false).applyStateUpdate()
                    publishResetSuccessState()
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

    private fun publishResetSuccessState() {
        uiState.value.copy(
            notificationState =
            NotificationState(
                message = "Reset mail sent successfully, please check your email to reset password",
                type = NotificationType.ERROR,
                autoDismiss = false
            )
        ).applyStateUpdate()
    }

    fun onNotificationClose() {
        uiState.value.copy(
            notificationState = null
        ).applyStateUpdate()
    }

    private fun resetSignInSate() {
        updateState(SignInUiState())
    }
}
