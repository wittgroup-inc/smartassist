package com.gowittgroup.smartassist.ui.auth.signup

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
import com.gowittgroup.smartassistlib.models.Resource
import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) : BaseViewModelWithStateIntentAndSideEffect<SignUpUiState, SignUpIntent, SignUpSideEffect>() {

    override fun getDefaultState(): SignUpUiState = SignUpUiState()

    override fun processIntent(intent: SignUpIntent) {
       // TODO need to implement
    }

    fun updateEmail(newEmail: String) {
        uiState.value.copy(email = newEmail).applyStateUpdate()
    }

    fun updatePassword(newPassword: String) {
        uiState.value.copy(password = newPassword).applyStateUpdate()
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        uiState.value.copy(confirmPassword = newConfirmPassword).applyStateUpdate()
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            if (uiState.value.password != uiState.value.confirmPassword) {
                throw Exception("Passwords do not match")
            }

            val res = authRepository.signUp(uiState.value.email, uiState.value.password)
            when (res) {
                is Resource.Success -> sendSideEffect(SignUpSideEffect.SignUpSuccess)
                is Resource.Error -> sendSideEffect(
                    SignUpSideEffect.SignUpFailed(
                        res.exception.message ?: "Something went wrong."
                    )
                )
                is Resource.Loading -> {}
            }
        }
    }


}