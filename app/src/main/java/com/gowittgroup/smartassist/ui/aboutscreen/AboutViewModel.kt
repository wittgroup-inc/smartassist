package com.gowittgroup.smartassist.ui.aboutscreen

import androidx.lifecycle.viewModelScope
import com.gowittgroup.smartassist.core.BaseViewModel

import com.gowittgroup.smartassistlib.repositories.authentication.AuthenticationRepository

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AboutUiState(
    val loading: Boolean = false,
    val error: String = "",
)

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository
) :
    BaseViewModel(authRepository) {
    private val _uiState = MutableStateFlow(AboutUiState(loading = true))
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()

    init {
        refreshAll()
    }

    private fun refreshAll() {

        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            var error: String = ""

            _uiState.update {
                it.copy(
                    loading = false,
                    error = error
                )
            }
        }
    }

    fun resetErrorMessage() {
        _uiState.update { it.copy(error = "") }
    }
}

