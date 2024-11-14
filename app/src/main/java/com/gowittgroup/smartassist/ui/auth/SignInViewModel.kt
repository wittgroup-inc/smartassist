package com.gowittgroup.smartassist.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.ui.BaseViewModel
import com.gowittgroup.smartassist.util.Session
import com.gowittgroup.smartassistlib.datasources.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInUiState(
    val email: String = "",
    val password: String = ""
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authService: AuthenticationService
) : BaseViewModel(authService) {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail) }
    }

    fun updatePassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword) }
    }

    fun onSignInClick() {
        viewModelScope.launch {
            authService.signIn(uiState.value.email, uiState.value.password)
        }
    }
}
