package com.gowittgroup.smartassist.ui.auth.signin

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModelWithStateIntentAndSideEffect
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
        uiState.value.copy(email = newEmail).applyStateUpdate()
    }

    fun updatePassword(newPassword: String) {
        uiState.value.copy(password = newPassword).applyStateUpdate()
    }

    fun onSignInClick() {
        viewModelScope.launch {
            when (val res = authRepository.signIn(uiState.value.email, uiState.value.password)) {
                is Resource.Success -> sendSideEffect(SignInSideEffect.SignInSuccess)
                is Resource.Error -> sendSideEffect(
                    SignInSideEffect.ShowError(
                        res.exception.message ?: "Something went wrong."
                    )
                )

                is Resource.Loading -> {}
            }
        }
    }

    override fun getDefaultState(): SignInUiState = SignInUiState()

    override fun processIntent(intent: SignInIntent) {
        TODO("Not yet implemented")
    }

    fun onResetPasswordClick() {
        viewModelScope.launch {

            when (val res = authRepository.resetPassword(email = uiState.value.email)) {
                is Resource.Success -> sendSideEffect(SignInSideEffect.RestPasswordSuccess)
                is Resource.Error -> sendSideEffect(
                    SignInSideEffect.ShowError(
                        res.exception.message ?: "Something went wrong."
                    )
                )

                is Resource.Loading -> {}
            }
        }
    }
}
